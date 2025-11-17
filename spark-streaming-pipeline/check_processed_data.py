from pyspark.sql import SparkSession
import pymongo
from datetime import datetime

def check_data_comprehensively():
    print("🔍 COMPREHENSIVE DATA CHECK")
    print("=" * 50)
    
    # Configuration MongoDB
    MONGODB_URI = "mongodb+srv://fatimazahraboukamar23_db_user:QTLfmphbMT8UvbDb@iotcluster.yv9yxtt.mongodb.net/"
    
    try:
        # Connexion directe avec pymongo
        client = pymongo.MongoClient(MONGODB_URI)
        
        # Vérifier les bases de données
        print("📊 Databases available:")
        for db_name in client.list_database_names():
            db = client[db_name]
            collections = db.list_collection_names()
            print(f"  🗄️ {db_name}: {collections}")
            
            # Compter les documents dans chaque collection
            for coll_name in collections:
                count = db[coll_name].count_documents({})
                print(f"    📁 {coll_name}: {count} documents")
                
                # Afficher un document sample si la collection n'est pas vide
                if count > 0:
                    sample = db[coll_name].find_one()
                    print(f"    📄 Sample: {list(sample.keys())}")
        
        # Vérification spécifique
        print("\n🎯 SPECIFIC CHECK:")
        tracksecure_db = client['tracksecure']
        
        # Collections attendues
        collections_to_check = ['sensor_data', 'processed_predictions']
        
        for coll_name in collections_to_check:
            if coll_name in tracksecure_db.list_collection_names():
                count = tracksecure_db[coll_name].count_documents({})
                print(f"✅ {coll_name}: {count} documents")
                
                if count > 0:
                    # Afficher les derniers documents
                    recent_docs = list(tracksecure_db[coll_name].find().sort("_id", -1).limit(3))
                    print(f"   📅 Recent documents:")
                    for doc in recent_docs:
                        doc_id = str(doc['_id'])[:10] + "..."
                        print(f"      - ID: {doc_id}, Timestamp: {doc.get('processing_time', 'N/A')}")
            else:
                print(f"❌ {coll_name}: Collection does not exist")
                
        client.close()
        
    except Exception as e:
        print(f"🚨 Error during comprehensive check: {e}")

if __name__ == "__main__":
    check_data_comprehensively()