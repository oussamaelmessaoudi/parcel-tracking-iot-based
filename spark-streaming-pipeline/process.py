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
# Configuration MongoDB Atlas
# -------------------------
CHECKPOINT_LOCATION = "./checkpoints/mongodb_sink_ml"
MONGODB_URI = "mongodb+srv://fatimazahraboukamar23_db_user:QTLfmphbMT8UvbDb@iotcluster.yv9yxtt.mongodb.net/tracksecure?retryWrites=true&w=majority"
MONGODB_DATABASE = "tracksecure"
MONGODB_TABLE = "parcel_predictions"

KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"

def cleanup_checkpoint():
    try:
        if os.path.exists(CHECKPOINT_LOCATION):
            shutil.rmtree(CHECKPOINT_LOCATION)
        os.makedirs(CHECKPOINT_LOCATION, exist_ok=True)
        print("✅ Checkpoint cleaned")
    except Exception as e:
        print(f"❌ Checkpoint cleanup failed: {e}")

def write_predictions_to_mongodb(batch_df, epoch_id):
    print(f"--- Writing Batch {epoch_id} to MongoDB Atlas ---")
    try:
        if batch_df.count() > 0:
            # Écrire dans MongoDB Atlas
            (batch_df.write
                .format("mongo")
                .option("uri", MONGODB_URI)
                .option("database", MONGODB_DATABASE)
                .option("collection", MONGODB_TABLE)
                .mode("append")
                .save())
            print(f"✅ Batch {epoch_id} written to MongoDB Atlas ({batch_df.count()} rows)")
        else:
            print(f"⚠️ Batch {epoch_id} empty - nothing to write")
    except Exception as e:
        print(f"❌ Batch {epoch_id} failed to write to MongoDB: {e}")

# -------------------------
# Main execution
# -------------------------
print("⏳ Waiting for services...")
time.sleep(10)

print("🚀 Starting Spark Session with MongoDB support...")

# Configuration Spark avec TOUS les packages nécessaires
spark = SparkSession.builder \
    .appName("ParcelTrackingMongoDB") \
    .config("spark.sql.adaptive.enabled", "true") \
    .config("spark.jars.packages", 
            "org.apache.spark:spark-sql-kafka-0-10_2.12:3.5.1,"
            "org.mongodb.spark:mongo-spark-connector_2.12:3.0.1") \
    .config("spark.sql.streaming.checkpointLocation", CHECKPOINT_LOCATION) \
    .getOrCreate()

spark.sparkContext.setLogLevel("WARN")

cleanup_checkpoint()

# Test MongoDB Atlas connection
try:
    test_df = spark.read \
        .format("mongo") \
        .option("uri", MONGODB_URI) \
        .option("database", MONGODB_DATABASE) \
        .option("collection", "sensor_data") \
        .load() \
        .limit(1)
    print("✅ MongoDB Atlas connection successful")
    print(f"📊 Sample data count: {test_df.count()}")
except Exception as e:
    print(f"❌ MongoDB Atlas connection failed: {e}")
    # On continue quand même pour le moment

# Load YOUR models - avec chemins relatifs
print("🤖 Loading YOUR saved models...")

def load_your_model(path, name):
    try:
        model = PipelineModel.load(path)
        print(f"✅ {name} loaded successfully")
        return model
    except Exception as e:
        print(f"❌ {name} failed to load: {e}")
        return None

# Utiliser les chemins relatifs pour vos modèles
kmeans_model = load_your_model("./models/kmeans_pipeline", "KMeans")
rf_model = load_your_model("./models/rf_pipeline", "Random Forest") 
iforest_model = load_your_model("./models/iforest_pipeline", "Isolation Forest")

# Schema for Kafka data - CORRESPOND À VOTRE PRODUCER
schema = StructType([
    StructField("package_id", StringType(), True),
    StructField("latitude", DoubleType(), True),
    StructField("longitude", DoubleType(), True),
    StructField("Speed", DoubleType(), True),       # match producer
    StructField("temperature", DoubleType(), True),
    StructField("Humidity", DoubleType(), True),    # match producer
    StructField("battery_level", DoubleType(), True),
    StructField("battery_status", StringType(), True),
    StructField("timestamp", StringType(), True)
])

# Read from Kafka
print("📖 Reading from Kafka topics...")

try:
    kafka_df = spark.readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP_SERVERS) \
        .option("subscribe", "gps,temperature,battery") \
        .option("startingOffsets", "latest") \
        .option("failOnDataLoss", "false") \
        .load()
    
    print("✅ Kafka connection successful")
    
except Exception as e:
    print(f"❌ Kafka connection failed: {e}")
    print("💡 Vérifiez que Kafka est démarré sur localhost:9092")
    spark.stop()
    exit(1)

# Parse JSON data from Kafka
parsed_df = kafka_df.selectExpr("CAST(value AS STRING) as value") \
    .select(from_json("value", schema).alias("data")) \
    .select(
        when(col("data.package_id").isNull(), "unknown").otherwise(col("data.package_id")).alias("package_id"),
        when(col("data.latitude").isNull(), 34.0208).otherwise(col("data.latitude")).alias("latitude"),
        when(col("data.longitude").isNull(), -6.8416).otherwise(col("data.longitude")).alias("longitude"),
        when(col("data.Speed").isNull(), 50.0).otherwise(col("data.Speed")).alias("Speed"),
        when(col("data.temperature").isNull(), 25.0).otherwise(col("data.temperature")).alias("temperature"),
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

print("📋 Features schema (what models will see):")
feature_df.printSchema()

# Apply YOUR models
print("🎯 Applying YOUR models...")
result_df = feature_df

# KMeans Clustering
if kmeans_model:
    try:
        print("🔮 Applying KMeans...")
        result_df = kmeans_model.transform(result_df)
        # Renommer selon votre entraînement
        if "prediction" in result_df.columns:
            result_df = result_df.withColumnRenamed("prediction", "traj_cluster")
        print("✅ KMeans applied successfully")
    except Exception as e:
        print(f"❌ KMeans failed: {e}")
        result_df = result_df.withColumn("traj_cluster", lit(0))
else:
    result_df = result_df.withColumn("traj_cluster", lit(0))

# Random Forest for Delay Prediction
if rf_model:
    try:
        print("🌲 Applying Random Forest...")
        result_df = rf_model.transform(result_df)
        # Rename prediction column based on your training
        if "prediction" in result_df.columns:
            result_df = result_df.withColumnRenamed("prediction", "delay_prediction")
            print("✅ Random Forest applied successfully")
        else:
            print("⚠️ RF no prediction column found")
            result_df = result_df.withColumn("delay_prediction", lit(0.0))
    except Exception as e:
        print(f"❌ Random Forest failed: {e}")
        result_df = result_df.withColumn("delay_prediction", lit(0.0))
else:
    result_df = result_df.withColumn("delay_prediction", lit(0.0))

# Isolation Forest for Anomaly Detection
if iforest_model:
    try:
        print("🕵️ Applying Isolation Forest...")
        result_df = iforest_model.transform(result_df)
        # Your training used 'anomaly_pred'
        if "anomaly_pred" in result_df.columns:
            result_df = result_df.withColumnRenamed("anomaly_pred", "anomaly_prediction")
            print("✅ Isolation Forest applied successfully")
        elif "prediction" in result_df.columns:
            result_df = result_df.withColumnRenamed("prediction", "anomaly_prediction")
            print("✅ Isolation Forest applied (using prediction column)")
        else:
            print("⚠️ IF no prediction column found")
            result_df = result_df.withColumn("anomaly_prediction", lit(0.0))
    except Exception as e:
        print(f"❌ Isolation Forest failed: {e}")
        result_df = result_df.withColumn("anomaly_prediction", lit(0.0))
else:
    result_df = result_df.withColumn("anomaly_prediction", lit(0.0))

# Final output for MongoDB Atlas
final_df = result_df \
    .withColumn("latitude", col("GPS Latitude")) \
    .withColumn("longitude", col("GPS Longitude")) \
    .withColumn("temperature", col("Temperature")) \
    .withColumn("humidity", col("Humidity")) \
    .withColumn("speed", col("Speed")) \
    .select(
        "package_id", 
        "event_time", 
        "latitude", 
        "longitude",
        "temperature", 
        "humidity",
        "speed",
        "battery_level", 
        "battery_status",
        "delay_prediction",
        "traj_cluster", 
        "anomaly_prediction",
        "Risk Factor",
        "Disruption Type",
        "Delivery Status"
    )

print("📊 Final output schema for MongoDB Atlas:")
final_df.printSchema()

# Afficher un échantillon en console pour debug
console_query = final_df.writeStream \
    .outputMode("append") \
    .format("console") \
    .option("truncate", "false") \
    .start()

# Start streaming to MongoDB Atlas
print("🚀 Starting streaming to MongoDB Atlas...")
query = final_df.writeStream \
    .outputMode("append") \
    .foreachBatch(write_predictions_to_mongodb) \
    .option("checkpointLocation", CHECKPOINT_LOCATION) \
    .trigger(processingTime="10 seconds") \
    .start()

print("✅ Streaming started with YOUR models to MongoDB Atlas!")
print("📡 Waiting for data from Kafka...")
print("💡 Make sure your MongoDB producer is running to send data")

query.awaitTermination()
console_query.awaitTermination()