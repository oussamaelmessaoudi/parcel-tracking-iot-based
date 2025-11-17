from pyspark.sql import SparkSession

# Your existing MongoDB connection
MONGODB_URI = "mongodb+srv://fatimazahraboukamar23_db_user:QTLfmphbMT8UvbDb@iotcluster.yv9yxtt.mongodb.net/tracksecure?retryWrites=true&w=majority"

def read_from_mongodb():
    spark = SparkSession.builder \
        .appName("ReadFromMongoDB") \
        .config("spark.mongodb.input.uri", MONGODB_URI) \
        .config("spark.jars.packages", "org.mongodb.spark:mongo-spark-connector_2.12:3.0.1") \
        .getOrCreate()
    
    # Read RAW data from MongoDB Cloud
    raw_df = spark.read \
        .format("mongo") \
        .option("database", "tracksecure") \
        .option("collection", "sensor_data") \
        .load()
    
    print("📊 RAW Data from MongoDB Cloud:")
    raw_df.show()
    print(f"Total records: {raw_df.count()}")
    
    return raw_df

if __name__ == "__main__":
    read_from_mongodb()