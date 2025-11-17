# producer_mongodb.py
import json
import time
from kafka import KafkaProducer
from pymongo import MongoClient
from datetime import datetime

# Configuration MongoDB Atlas
MONGODB_URI = "mongodb+srv://fatimazahraboukamar23_db_user:QTLfmphbMT8UvbDb@iotcluster.yv9yxtt.mongodb.net/tracksecure?retryWrites=true&w=majority"
KAFKA_BOOTSTRAP_SERVERS = 'localhost:9092'

print(f"🔗 Connexion à MongoDB Atlas")
print(f"🔗 Connexion à Kafka: {KAFKA_BOOTSTRAP_SERVERS}")

# Initialisation Kafka Producer
producer = KafkaProducer(
    bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
    value_serializer=lambda v: json.dumps(v, default=str).encode('utf-8')
)

# Connexion MongoDB Atlas
client = MongoClient(MONGODB_URI)
db = client.tracksecure

def find_sensor_collection():
    """Trouve la collection contenant les données de capteurs"""
    collections = db.list_collection_names()
    print(f"📁 Collections disponibles: {collections}")
    
    # Priorité des noms de collections possibles
    possible_collections = ['sensor_data', 'raw_sensor_data', 'iot_data', 'device_data']
    
    for col_name in possible_collections:
        if col_name in collections:
            print(f"✅ Collection trouvée: {col_name}")
            return db[col_name]
    
    # Si aucune collection standard n'est trouvée, prendre la première
    if collections:
        print(f"✅ Utilisation de la première collection: {collections[0]}")
        return db[collections[0]]
    
    print("❌ Aucune collection trouvée")
    return None

def transform_to_kafka(mongo_doc):
    """Transforme le document MongoDB en données pour Kafka - AVEC BONS NOMS DE COLONNES"""
    
    print(f"🔍 Traitement du document: {mongo_doc.get('_id')}")
    
    # Extraire les données selon différentes structures possibles
    package_id = mongo_doc.get('deviceId') or mongo_doc.get('package_id') or 'unknown'
    
    # AJOUTER CETTE LIGNE pour le timestamp
    timestamp = mongo_doc.get('receivedAt') or datetime.now().isoformat()
    
    # Structure 1: Données imbriquées (dhtData, gpsData)
    if 'dhtData' in mongo_doc:
        dht_data = mongo_doc.get('dhtData', {})
        temperature = dht_data.get('temperature', 25.0)
        humidity = dht_data.get('humidity', 55.0)
    else:
        # Structure 2: Données plates
        temperature = mongo_doc.get('temperature', 25.0)
        humidity = mongo_doc.get('humidity', 55.0)
    
    if 'gpsData' in mongo_doc:
        gps_data = mongo_doc.get('gpsData', {})
        latitude = gps_data.get('latitude', 34.0208)
        longitude = gps_data.get('longitude', -6.8416)
        speed = gps_data.get('speed', 50.0)
    else:
        # Structure 2: Données plates
        latitude = mongo_doc.get('latitude', 34.0208)
        longitude = mongo_doc.get('longitude', -6.8416)
        speed = mongo_doc.get('speed', 50.0)
    
    # Données de batterie
    battery_level = mongo_doc.get('batteryLevel') or mongo_doc.get('battery_level', 95.0)
    battery_status = "healthy" if battery_level > 20 else "low"
    
    # Créer le message pour Kafka - AVEC LES BONS NOMS DE COLONNES POUR SPARK
    kafka_message = {
    "package_id": package_id,
    "latitude": float(latitude),
    "longitude": float(longitude),
    "Speed": float(speed),                    # ← "Speed" avec majuscule
    "temperature": float(temperature),
    "Humidity": float(humidity),              # ← "Humidity" avec majuscule
    "battery_level": float(battery_level),
    "battery_status": battery_status,
    "timestamp": timestamp
}
    
    return kafka_message

def send_to_kafka_topics(message):
    """Envoie le même message aux trois topics Kafka"""
    try:
        # Envoyer aux trois topics
        producer.send('gps', value=message)
        producer.send('temperature', value=message)
        producer.send('battery', value=message)
        producer.flush()
        
        print(f"✅ Données envoyées pour {message['package_id']}")
        print(f"   📍 GPS: ({message['latitude']:.4f}, {message['longitude']:.4f})")
        print(f"   🚗 Speed: {message['Speed']} km/h")
        print(f"   🌡️  Temp: {message['temperature']}°C")
        print(f"   💧 Humidity: {message['Humidity']}%")
        print(f"   🔋 Battery: {message['battery_level']}% ({message['battery_status']})")
        print("   " + "-" * 50)
        
        return True
    except Exception as e:
        print(f"❌ Erreur envoi Kafka: {e}")
        return False

def main():
    print("🚀 Démarrage du Producer MongoDB Atlas → Kafka")
    
    # Trouver la collection de capteurs
    collection = find_sensor_collection()
    if collection is None:  # ← CORRECTION ICI: "is None" au lieu de "not collection"
        print("❌ Aucune collection trouvée dans la base de données!")
        return
    
    print(f"📊 Surveillance de la collection: {collection.name}")
    
    # Compter les documents existants
    total_docs = collection.count_documents({})
    print(f"📄 Nombre total de documents: {total_docs}")
    
    # Pour suivre le dernier document traité
    last_processed_id = None
    
    try:
        while True:
            # Vérifier la connexion MongoDB
            client.admin.command('ping')
            
            # Construire la requête pour les nouveaux documents
            query = {}
            if last_processed_id:
                query['_id'] = {'$gt': last_processed_id}
                print(f"🔍 Recherche de nouveaux documents après ID: {last_processed_id}")
            else:
                # Pour le premier lancement, prendre tous les documents
                print("💡 Lecture de tous les documents existants...")
                query = {}
            
            # Récupérer les nouveaux documents
            new_docs = collection.find(query).sort('_id', 1)
            processed_count = 0
            
            for doc in new_docs:
                # Transformer le document MongoDB en message Kafka
                kafka_message = transform_to_kafka(doc)
                
                # Envoyer aux topics Kafka
                if send_to_kafka_topics(kafka_message):
                    last_processed_id = doc['_id']
                    processed_count += 1
            
            if processed_count > 0:
                print(f"📨 {processed_count} nouveau(x) document(s) traité(s) depuis MongoDB")
            else:
                print("⏳ Aucun nouveau document dans MongoDB...")
                if last_processed_id is None and total_docs > 0:
                    print("💡 Documents existants mais non traités? Vérifiez la structure des données.")
                elif last_processed_id is None:
                    print("💡 Aucun document existant. Vous pouvez en ajouter via insert_test_data.py")
            
            # Attendre avant de vérifier à nouveau
            print(f"⏱️  Attente de 10 secondes...")
            time.sleep(10)
            
    except KeyboardInterrupt:
        print("\n🛑 Arrêt du producer...")
    except Exception as e:
        print(f"❌ Erreur générale: {e}")
    finally:
        producer.close()
        client.close()
        print("✅ Producer arrêté proprement")

if __name__ == "__main__":
    main()