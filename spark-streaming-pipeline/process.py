from pyspark.sql import SparkSession
from pyspark.sql.functions import col, from_json, current_timestamp, expr
from pyspark.sql.types import StructType, StringType, DoubleType, TimestampType
import time

# --- Configuration PostgreSQL ---
db_url = "jdbc:postgresql://postgres:5432/parcel_db"
db_table = "parcel_data"
db_properties = {
    "user": "sparkuser",
    "password": "sparkpassword",
    "driver": "org.postgresql.Driver"
}

# --- Fonction pour écrire le batch joint dans PostgreSQL ---
def write_joined_batch_to_postgres(batch_df, epoch_id):
    # Cette fonction est appelée pour chaque micro-batch
    print(f"--- Writing Batch {epoch_id} ---")

    # Écrire le DataFrame dans la table PostgreSQL
    # 'mode("append")' ajoute les nouvelles données
    batch_df.write \
        .mode("append") \
        .jdbc(url=db_url, table=db_table, properties=db_properties)

    print(f"--- Batch {epoch_id} written successfully ---")


# Délai pour laisser PostgreSQL et Kafka démarrer
time.sleep(15)
print("Spark script started...")

# Créer la session Spark
spark = SparkSession.builder \
    .appName("ParcelTrackingStreaming") \
    .config("spark.driver.memory", "4g") \
    .getOrCreate() # Les packages Kafka et PostgreSQL sont chargés par docker-compose

spark.sparkContext.setLogLevel("ERROR")
print("Spark version:", spark.version)
print("Spark session created!")

# Définir les schémas
gps_schema = StructType().add("package_id", StringType()).add("latitude", DoubleType()).add("longitude", DoubleType())
temperature_schema = StructType().add("package_id", StringType()).add("temperature", DoubleType())
battery_schema = StructType().add("package_id", StringType()).add("battery_level", DoubleType())

# Lire les 3 topics depuis Kafka
kafka_df = spark.readStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", "kafka:29092") \
    .option("subscribe", "gps,temperature,battery") \
    .option("startingOffsets", "latest") \
    .load()

print("Successfully connected to Kafka topics.")

# Convertir les données et ajouter un timestamp
kafka_df_parsed = kafka_df \
    .selectExpr("topic", "CAST(value AS STRING) as value") \
    .withColumn("timestamp", current_timestamp()) # Ajoute l'heure de traitement

# Parser et séparer les messages
gps_df = kafka_df_parsed \
    .filter(col("topic") == "gps") \
    .select(from_json(col("value"), gps_schema).alias("data"), col("timestamp")) \
    .select("data.*", "timestamp") \
    .withWatermark("timestamp", "1 minute")

temperature_df = kafka_df_parsed \
    .filter(col("topic") == "temperature") \
    .select(from_json(col("value"), temperature_schema).alias("data"), col("timestamp").alias("temp_time")) \
    .select("data.*", "temp_time") \
    .withWatermark("temp_time", "1 minute")

battery_df = kafka_df_parsed \
    .filter(col("topic") == "battery") \
    .select(from_json(col("value"), battery_schema).alias("data"), col("timestamp").alias("batt_time")) \
    .select("data.*", "batt_time") \
    .withWatermark("batt_time", "1 minute")

# Joindre les streams (PARTIE CORRIGÉE)
print("Joining streams...")

# Créer des alias (surnoms) pour résoudre l'ambiguïté
gps_aliased = gps_df.alias("gps")
temp_aliased = temperature_df.alias("temp")
batt_aliased = battery_df.alias("batt")

joined_df = gps_aliased.join(
    temp_aliased,
    expr("""
        gps.package_id = temp.package_id AND 
        temp.temp_time >= gps.timestamp - interval 2 minutes AND
        temp.temp_time <= gps.timestamp + interval 2 minutes
    """),
    "leftOuter"
).join(
    batt_aliased,
    expr("""
        gps.package_id = batt.package_id AND
        batt.batt_time >= gps.timestamp - interval 2 minutes AND
        batt.batt_time <= gps.timestamp + interval 2 minutes
    """),
    "leftOuter"
).select(
    col("gps.package_id").alias("package_id"), # Sélectionner la colonne principale
    col("gps.timestamp").alias("timestamp"),
    col("gps.latitude"),
    col("gps.longitude"),
    col("temp.temperature"),
    col("batt.battery_level")
)

print("Stream join defined.")

# Écrire le stream FINAL vers PostgreSQL
query = joined_df.writeStream \
    .outputMode("append") \
    .foreachBatch(write_joined_batch_to_postgres) \
    .option("checkpointLocation", "/tmp/spark-checkpoints/postgres_sink") \
    .start()

# Attendre la fin
print("Starting stream to PostgreSQL... waiting for termination.")
query.awaitTermination()