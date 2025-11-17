# read_from_mongo.py - TRÈS SIMPLE!
from pyspark.sql import SparkSession

print("🚀 Démarrage du script...")

# 1. Créer une session Spark
spark = SparkSession.builder \
    .appName("ReadFromMongoDB") \
    .config("spark.jars.packages", "org.mongodb.spark:mongo-spark-connector_2.12:3.0.1") \
    .getOrCreate()

print("✅ Session Spark créée")

# 2. Lire depuis MongoDB Cloud
try:
    df = spark.read \
        .format("mongo") \
        .option("uri", "mongodb+srv://fatimazahraboukamar23_db_user:QTLfmphbMT8UvbDb@iotcluster.yv9yxtt.mongodb.net/tracksecure.sensor_data") \
        .load()
    
    print("✅ Connexion MongoDB réussie!")
    print(f"📊 Nombre total d'enregistrements: {df.count()}")
    
    # Afficher les premières lignes
    print("📋 Premiers enregistrements:")
    df.show(5)
    
except Exception as e:
    print(f"❌ Erreur: {e}")

print("🏁 Script terminé")