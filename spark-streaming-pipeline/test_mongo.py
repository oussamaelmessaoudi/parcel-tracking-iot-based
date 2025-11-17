# test_mongo.py - TEST TRÈS SIMPLE
from pyspark.sql import SparkSession

print("🚀 Test MongoDB Cloud...")

spark = SparkSession.builder \
    .appName("TestMongo") \
    .config("spark.jars.packages", "org.mongodb.spark:mongo-spark-connector_2.12:3.0.1") \
    .getOrCreate()

try:
    # Lire depuis MongoDB Cloud
    df = spark.read \
        .format("mongo") \
        .option("uri", "mongodb+srv://fatimazahraboukamar23_db_user:QTLfmphbMT8UvbDb@iotcluster.yv9yxtt.mongodb.net/tracksecure.sensor_data") \
        .load()
    
    print(f"✅ SUCCÈS! {df.count()} documents dans MongoDB Cloud")
    print("📋 Exemple des données:")
    df.select("deviceId", "dhtData.temperature", "receivedAt").show(3)
    
except Exception as e:
    print(f"❌ ERREUR: {e}")

spark.stop()