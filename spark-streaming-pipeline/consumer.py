import os
import shutil
import numpy as np

from pyspark.sql import SparkSession
from pyspark.sql.functions import (
    col, from_json, current_timestamp, when, lit, to_json, struct,
    hour, dayofweek, udf, max as spark_max
)
from pyspark.sql.types import StructType, StructField, StringType, DoubleType

from pyspark.ml import PipelineModel
from pyspark.ml.feature import VectorAssembler, StandardScalerModel
from pyspark.ml.clustering import KMeansModel

# ============================================================
# 1. CONFIG
# ============================================================
CHECKPOINT_CONSOLE = "/app/checkpoints/console_sink"
CHECKPOINT_KAFKA = "/app/checkpoints/kafka_sink"
KAFKA_BOOTSTRAP = os.environ.get("KAFKA_BOOTSTRAP", "kafka:29092")

RF_MODEL_PATH = "/app/models/rf_model"
KMEANS_MODEL_PATH = "/app/models/kmeans_model"
SCALER_ANOM_PATH = "/app/models/scaler_anom"

INPUT_TOPIC = "parcel_data"
ANOMALIES_TOPIC = "anomalies"

# ============================================================
# 2. CLEANUP + SPARK SESSION
# ============================================================
def cleanup_checkpoints():
    for path in [CHECKPOINT_CONSOLE, CHECKPOINT_KAFKA]:
        shutil.rmtree(path, ignore_errors=True)
        os.makedirs(path, exist_ok=True)

cleanup_checkpoints()

spark = SparkSession.builder \
    .appName("ParcelStreamingWithModels") \
    .config("spark.sql.shuffle.partitions", "4") \
    .config("spark.streaming.stopGracefullyOnShutdown", "true") \
    .config("spark.jars.packages",
            "org.apache.spark:spark-sql-kafka-0-10_2.12:3.5.0") \
    .getOrCreate()

spark.sparkContext.setLogLevel("ERROR")

print("**************************************************")
print("!!!   CONSUMER RUNNING — MODELS LOADED         !!!")
print("**************************************************")

# ============================================================
# 3. LOAD MODELS
# ============================================================
rf_model = None
kmeans_model = None
scaler_anom_model = None
b_centers = None

print("\n--- Loading Models ---")

try:
    rf_model = PipelineModel.load(RF_MODEL_PATH)
    print("SUCCESS: RF model loaded.")
except:
    print(f"WARNING: RF model NOT found at {RF_MODEL_PATH}")

try:
    kmeans_model = KMeansModel.load(KMEANS_MODEL_PATH)
    centers = [c.tolist() for c in kmeans_model.clusterCenters()]
    b_centers = spark.sparkContext.broadcast(centers)
    print("SUCCESS: KMeans model loaded.")
except:
    print(f"WARNING: KMeans model NOT found at {KMEANS_MODEL_PATH}")

try:
    scaler_anom_model = StandardScalerModel.load(SCALER_ANOM_PATH)
    print("SUCCESS: Scaler loaded.")
except:
    print(f"WARNING: Scaler NOT found at {SCALER_ANOM_PATH}")

# ============================================================
# 4. STREAMING SOURCE
# ============================================================
schema = StructType([
    StructField("package_id", StringType(), True),
    StructField("timestamp", StringType(), True),
    StructField("gps_latitude", DoubleType(), True),
    StructField("gps_longitude", DoubleType(), True),
    StructField("temperature", DoubleType(), True),
    StructField("humidity", DoubleType(), True),
    StructField("speed", DoubleType(), True)
])

kafka_df = spark.readStream.format("kafka") \
    .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP) \
    .option("subscribe", INPUT_TOPIC) \
    .option("startingOffsets", "latest") \
    .load()

parsed_df = kafka_df.selectExpr("CAST(value AS STRING) AS value") \
    .select(from_json(col("value"), schema).alias("data")) \
    .select(
    when(col("data.package_id").isNull(), "unknown").otherwise(col("data.package_id")).alias("package_id"),
    when(col("data.gps_latitude").isNull(), 34.0).otherwise(col("data.gps_latitude")).alias("GPS Latitude"),
    when(col("data.gps_longitude").isNull(), -6.8).otherwise(col("data.gps_longitude")).alias("GPS Longitude"),
    when(col("data.speed").isNull(), 0.0).otherwise(col("data.speed")).alias("Speed"),
    when(col("data.temperature").isNull(), 25.0).otherwise(col("data.temperature")).alias("Temperature"),
    when(col("data.humidity").isNull(), 55.0).otherwise(col("data.humidity")).alias("Humidity"),
    current_timestamp().alias("event_time")
)

# ============================================================
# 5. FEATURE ENGINEERING
# ============================================================
feat = parsed_df \
    .withColumn("hour_of_day", hour("event_time")) \
    .withColumn("day_of_week", dayofweek("event_time")) \
    .withColumn("temp_alert",
                when((col("Temperature") > 40) | (col("Temperature") < -10), 1.0).otherwise(0.0))

cat_cols = ["Delivery Status", "Stage", "Route ID", "Entity", "Risk Factor", "Disruption Type"]
for c in cat_cols:
    feat = feat.withColumn(c, lit("unknown"))

feat = feat.withColumn("Delay_Time_Num", lit(0.0)) \
    .withColumn("Cost_Num", lit(0.0)) \
    .withColumn("time_diff_secs", lit(0.0)) \
    .withColumn("dist_approx", lit(0.0)) \
    .withColumn("speed_calc", col("Speed"))

# ============================================================
# 6. RANDOM FOREST PREDICTION
# ============================================================
if rf_model:
    try:
        df_pred = rf_model.transform(feat).withColumnRenamed("prediction", "rf_delay_pred")
    except Exception as e:
        print(f"RF Prediction ERROR: {e}")
        df_pred = feat.withColumn("rf_delay_pred", lit(0))
else:
    df_pred = feat.withColumn("rf_delay_pred", lit(0))

# ============================================================
# 7. KMEANS ANOMALY DETECTION
# ============================================================
if kmeans_model and scaler_anom_model:
    try:
        k_cols = ["GPS Latitude", "GPS Longitude", "Temperature", "Humidity", "Speed"]

        assembler_k = VectorAssembler(inputCols=k_cols, outputCol="anom_features_raw")
        df_pred = assembler_k.transform(df_pred)

        df_pred = scaler_anom_model.transform(df_pred)

        df_pred = kmeans_model.transform(df_pred).withColumnRenamed("prediction", "cluster_id")

    except Exception as e:
        print(f"KMeans ERROR: {e}")
        df_pred = df_pred.withColumn("cluster_id", lit(-1))
else:
    df_pred = df_pred.withColumn("cluster_id", lit(-1))

# ============================================================
# 8. ANOMALY SCORE
# ============================================================
def kdist_sq(vec, cluster_id):
    if vec is None or cluster_id is None or b_centers is None:
        return 0.0
    try:
        arr = np.array(vec.toArray())
        center = np.array(b_centers.value[int(cluster_id)])
        return float(np.sum((arr - center) ** 2))
    except:
        return 0.0

kdist_udf = udf(kdist_sq, DoubleType())

df_pred = df_pred.withColumn("KMeans_Anomaly_Score",
                             kdist_udf(col("features_anom"), col("cluster_id")))

# ============================================================
# 9. FINAL LABELS
# ============================================================
# Streaming-compatible threshold
THRESHOLD = float(os.environ.get("ANOMALY_THRESHOLD", 2500.0))

result = df_pred \
    .withColumn("IF_Alert",
                when((col("temp_alert") == 1) |
                     (col("KMeans_Anomaly_Score") > THRESHOLD),
                     "ANOMALY").otherwise("NORMAL")) \
    .withColumn("Delay_Status",
                when(col("rf_delay_pred") == 1, "DELAY_PREDICTED").otherwise("ON_TIME"))

# ============================================================
# 10. OUTPUT STREAMS
# ============================================================
final_output_df = result.select(
    "package_id", "event_time",
    "GPS Latitude", "GPS Longitude",
    "Temperature", "Humidity", "Speed",
    "Delay_Status", "IF_Alert",
    "KMeans_Anomaly_Score"
)

anomaly_alerts_df = result.filter(
    (col("Delay_Status") == "DELAY_PREDICTED") |
    (col("IF_Alert") == "ANOMALY")
).select(
    col("package_id").alias("key"),
    to_json(struct(
        "event_time",
        "package_id",
        "Delay_Status",
        "IF_Alert",
        "KMeans_Anomaly_Score",
        col("Temperature").alias("temperature"),
        col("GPS Latitude").alias("latitude"),
        col("GPS Longitude").alias("longitude")
    )).alias("value")
)

print("\nStarting Streams...\n")

q1 = final_output_df.writeStream \
    .format("console") \
    .outputMode("append") \
    .option("truncate", "false") \
    .option("checkpointLocation", CHECKPOINT_CONSOLE) \
    .start()

q2 = anomaly_alerts_df.writeStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", KAFKA_BOOTSTRAP) \
    .option("topic", ANOMALIES_TOPIC) \
    .option("checkpointLocation", CHECKPOINT_KAFKA) \
    .outputMode("append") \
    .start()

spark.streams.awaitAnyTermination()
