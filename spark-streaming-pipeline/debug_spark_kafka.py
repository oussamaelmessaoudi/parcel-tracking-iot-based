from pyspark.sql import SparkSession
from pyspark.streaming import StreamingContext
import json

def check_spark_kafka_connection():
    try:
        # Configuration Spark
        spark = SparkSession.builder \
            .appName("KafkaDebug") \
            .config("spark.jars.packages", "org.apache.spark:spark-sql-kafka-0-10_2.12:3.4.0") \
            .config("spark.sql.adaptive.enabled", "true") \
            .getOrCreate()
        
        spark.sparkContext.setLogLevel("INFO")
        
        # Test de connexion Kafka
        print("🔍 Test de connexion aux topics Kafka...")
        
        # Lire depuis chaque topic
        topics = ["gps", "temperature", "battery"]
        
        for topic in topics:
            try:
                df = spark \
                    .readStream \
                    .format("kafka") \
                    .option("kafka.bootstrap.servers", "localhost:9092") \
                    .option("subscribe", topic) \
                    .option("startingOffsets", "earliest") \
                    .load()
                
                print(f"✅ Topic '{topic}' accessible")
                
                # Afficher le schéma
                print(f"📋 Schéma du topic {topic}:")
                df.printSchema()
                
                # Compter les messages
                count_query = df.writeStream \
                    .format("memory") \
                    .queryName(f"debug_{topic}") \
                    .outputMode("append") \
                    .start()
                
                count_query.awaitTermination(5)  # Attendre 5 secondes
                
                result = spark.sql(f"SELECT COUNT(*) as count FROM debug_{topic}")
                count = result.collect()[0]['count']
                print(f"📊 Messages dans '{topic}': {count}")
                
                count_query.stop()
                
            except Exception as e:
                print(f"❌ Erreur avec le topic '{topic}': {str(e)}")
        
        spark.stop()
        
    except Exception as e:
        print(f"🚨 Erreur générale Spark: {str(e)}")

if __name__ == "__main__":
    check_spark_kafka_connection()