# process.py
import os
import time
import shutil
from pyspark.sql import SparkSession
from pyspark.sql.functions import (
    col, from_json, current_timestamp, when, lit, sqrt, pow
)
from pyspark.sql.types import (
    StructType, StructField, StringType, DoubleType, TimestampType, IntegerType
)
from pyspark.ml import PipelineModel

# -------------------------
# Configuration
# -------------------------
CHECKPOINT_LOCATION = "/app/checkpoints/postgres_sink_ml"
DB_URL = "jdbc:postgresql://postgres:5432/parcel_db"
DB_TABLE = "parcel_predictions"
DB_USER = "sparkuser"
DB_PASSWORD = "sparkpassword"
DB_PROPERTIES = {"user": DB_USER, "password": DB_PASSWORD, "driver": "org.postgresql.Driver"}
KAFKA_BOOTSTRAP_SERVERS = "kafka:29092"

def cleanup_checkpoint():
    try:
        if os.path.exists(CHECKPOINT_LOCATION):
            shutil.rmtree(CHECKPOINT_LOCATION)
        os.makedirs(CHECKPOINT_LOCATION, exist_ok=True)
        print(" Checkpoint cleaned")
    except Exception as e:
        print(f" Checkpoint cleanup failed: {e}")

def write_predictions_to_postgres(batch_df, epoch_id):
    print(f"--- Writing Batch {epoch_id} ---")
    try:
        if batch_df.count() > 0:
            batch_df.write.jdbc(url=DB_URL, table=DB_TABLE, mode="append", properties=DB_PROPERTIES)
            print(f" Batch {epoch_id} written ({batch_df.count()} rows)")
        else:
            print(f" Batch {epoch_id} empty")
    except Exception as e:
        print(f" Batch {epoch_id} failed: {e}")

# -------------------------
# Main execution
# -------------------------
print(" Waiting for services...")
time.sleep(30)

print(" Starting Spark...")
spark = SparkSession.builder \
    .appName("ParcelTrackingWithYourModels") \
    .config("spark.sql.adaptive.enabled", "false") \
    .getOrCreate()

spark.sparkContext.setLogLevel("INFO")

cleanup_checkpoint()

# Check PostgreSQL
try:
    test_df = spark.read.jdbc(url=DB_URL, table=DB_TABLE, properties=DB_PROPERTIES)
    print(f"PostgreSQL OK: {test_df.count()} rows")
except Exception as e:
    print(f"PostgreSQL failed: {e}")
    spark.stop()
    exit(1)

# Load YOUR models
print("Loading YOUR saved models...")

def load_your_model(path, name):
    try:
        model = PipelineModel.load(path)
        print(f"{name} loaded successfully")
        return model
    except Exception as e:
        print(f"{name} failed to load: {e}")
        return None

kmeans_model = load_your_model("/app/models/kmeans_pipeline", "KMeans")
rf_model = load_your_model("/app/models/rf_pipeline", "Random Forest")
iforest_model = load_your_model("/app/models/iforest_pipeline", "Isolation Forest")

# Schema for Kafka data
schema = StructType([
    StructField("package_id", StringType(), True),
    StructField("latitude", DoubleType(), True),
    StructField("longitude", DoubleType(), True),
    StructField("Speed", DoubleType(), True),
    StructField("temperature", DoubleType(), True),
    StructField("Humidity", DoubleType(), True),
    StructField("battery_level", DoubleType(), True),
    StructField("battery_status", StringType(), True),
    StructField("timestamp", StringType(), True)
])

# Read Kafka
print("Reading from Kafka...")
kafka_df = spark.readStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP_SERVERS) \
    .option("subscribe", "gps,temperature,battery") \
    .option("startingOffsets", "latest") \
    .load()

# Parse data
parsed_df = kafka_df.selectExpr("CAST(value AS STRING) as value") \
    .select(from_json("value", schema).alias("data")) \
    .select(
    # Base fields from Kafka
    when(col("data.package_id").isNull(), "unknown").otherwise(col("data.package_id")).alias("package_id"),
    when(col("data.latitude").isNull(), 34.0208).otherwise(col("data.latitude")).alias("GPS Latitude"),  # WITH SPACE
    when(col("data.longitude").isNull(), -6.8416).otherwise(col("data.longitude")).alias("GPS Longitude"),  # WITH SPACE
    when(col("data.Speed").isNull(), 50.0).otherwise(col("data.Speed")).alias("Speed"),
    when(col("data.temperature").isNull(), 25.0).otherwise(col("data.temperature")).alias("Temperature"),
    when(col("data.Humidity").isNull(), 55.0).otherwise(col("data.Humidity")).alias("Humidity"),
    when(col("data.battery_level").isNull(), 95.0).otherwise(col("data.battery_level")).alias("battery_level"),
    when(col("data.battery_status").isNull(), "healthy").otherwise(col("data.battery_status")).alias("battery_status"),
    current_timestamp().alias("event_time")
)

print("🔧 Creating features EXACTLY like your training data...")

# Create the SAME features your models were trained with
feature_df = parsed_df \
    .withColumn("dist_approx", lit(0.0)) \
    .withColumn("speed_calc", col("Speed")) \
    .withColumn("time_diff_secs", lit(0.0)) \
    .withColumn("battery_low", when(col("battery_level") < 20, 1.0).otherwise(0.0)) \
    .withColumn("temp_alert", when(col("Temperature") > 40, 1.0).otherwise(0.0)) \
    .withColumn("Route ID", lit("route_001")) \
    .withColumn("Risk Factor",
                when(col("Speed") > 80, "HIGH")
                .when(col("Speed") > 50, "MEDIUM")
                .otherwise("LOW")) \
    .withColumn("Disruption Type",
                when(col("battery_level") < 20, "BATTERY_LOW")
                .when(col("Speed") == 0, "STOPPED")
                .otherwise("NONE")) \
    .withColumn("Delivery Status", lit("IN_TRANSIT")) \
    .withColumn("Stage", lit("TRANSPORT")) \
    .withColumn("delayed_label", lit(0))  # Required for RF model

print("Features schema (what models will see):")
feature_df.printSchema()

# Apply YOUR models
print("Applying YOUR models...")
result_df = feature_df

# KMeans
if kmeans_model:
    try:
        print("Applying KMeans...")
        result_df = kmeans_model.transform(result_df)
        print("KMeans applied")
    except Exception as e:
        print(f"KMeans failed: {e}")
        result_df = result_df.withColumn("traj_cluster", lit(0))
else:
    result_df = result_df.withColumn("traj_cluster", lit(0))

# Random Forest
if rf_model:
    try:
        print("Applying Random Forest...")
        result_df = rf_model.transform(result_df)
        # Rename prediction column based on your training
        if "prediction" in result_df.columns:
            result_df = result_df.withColumnRenamed("prediction", "delay_prediction")
            print("Random Forest applied")
        else:
            print("RF no prediction column")
            result_df = result_df.withColumn("delay_prediction", lit(0.0))
    except Exception as e:
        print(f"RF failed: {e}")
        result_df = result_df.withColumn("delay_prediction", lit(0.0))
else:
    result_df = result_df.withColumn("delay_prediction", lit(0.0))

# Isolation Forest
if iforest_model:
    try:
        print("Applying Isolation Forest...")
        result_df = iforest_model.transform(result_df)
        # Your training used 'anomaly_pred'
        if "anomaly_pred" in result_df.columns:
            result_df = result_df.withColumnRenamed("anomaly_pred", "anomaly_prediction")
            print("Isolation Forest applied")
        elif "prediction" in result_df.columns:
            result_df = result_df.withColumnRenamed("prediction", "anomaly_prediction")
            print("Isolation Forest applied (using prediction)")
        else:
            print("IF no prediction column")
            result_df = result_df.withColumn("anomaly_prediction", lit(0.0))
    except Exception as e:
        print(f"IF failed: {e}")
        result_df = result_df.withColumn("anomaly_prediction", lit(0.0))
else:
    result_df = result_df.withColumn("anomaly_prediction", lit(0.0))

# Final output for PostgreSQL
final_df = result_df \
    .withColumn("latitude", col("GPS Latitude")) \
    .withColumn("longitude", col("GPS Longitude")) \
    .withColumn("temperature", col("Temperature")) \
    .select(
    "package_id", "event_time", "latitude", "longitude",
    "temperature", "battery_level", "delay_prediction",
    "traj_cluster", "anomaly_prediction"
)

print("Final output schema:")
final_df.printSchema()

# Start streaming
print("Starting streaming to PostgreSQL...")
query = final_df.writeStream \
    .outputMode("append") \
    .foreachBatch(write_predictions_to_postgres) \
    .option("checkpointLocation", CHECKPOINT_LOCATION) \
    .trigger(processingTime="10 seconds") \
    .start()

print("Streaming started with YOUR models!")
print("Waiting for data...")
query.awaitTermination()