# insert_test_data.py
from pymongo import MongoClient
from datetime import datetime, timedelta
import random
import time

# Configuration MongoDB Atlas
MONGODB_URI = "mongodb+srv://fatimazahraboukamar23_db_user:QTLfmphbMT8UvbDb@iotcluster.yv9yxtt.mongodb.net/tracksecure?retryWrites=true&w=majority"

def insert_single_document():
    """Insère un seul document de test"""
    client = MongoClient(MONGODB_URI)
    db = client.tracksecure
    
    # Utiliser la collection sensor_data (celle que votre producer a détectée)
    collection = db.sensor_data
    
    # Générer des données réalistes avec des variations
    base_lat = 34.0208  # Casablanca
    base_lon = -6.8416
    base_temp = 25.0
    
    # Simuler un mouvement progressif
    lat_variation = random.uniform(-0.01, 0.01)
    lon_variation = random.uniform(-0.01, 0.01)
    temp_variation = random.uniform(-2, 2)
    
    # Données de test réalistes
    test_data = {
        "deviceId": "test-device-001",
        "dhtData": {
            "temperature": round(base_temp + temp_variation, 1),
            "humidity": round(55.0 + random.uniform(-10, 10), 1),
            "timestamp": datetime.now().isoformat()
        },
        "gpsData": {
            "latitude": round(base_lat + lat_variation, 6),
            "longitude": round(base_lon + lon_variation, 6),
            "speed": round(50.0 + random.uniform(-15, 15), 1),
            "timestamp": datetime.now().isoformat()
        },
        "batteryLevel": round(95.0 - random.uniform(0, 5), 1),  # Batterie qui se décharge lentement
        "receivedAt": datetime.now().isoformat()
    }
    
    try:
        result = collection.insert_one(test_data)
        print(f"✅ Données de test insérées avec ID: {result.inserted_id}")
        print("📊 Document inséré:")
        print(f"   📍 Location: ({test_data['gpsData']['latitude']}, {test_data['gpsData']['longitude']})")
        print(f"   🌡️  Temperature: {test_data['dhtData']['temperature']}°C")
        print(f"   💧 Humidity: {test_data['dhtData']['humidity']}%")
        print(f"   🚗 Speed: {test_data['gpsData']['speed']} km/h")
        print(f"   🔋 Battery: {test_data['batteryLevel']}%")
        print(f"   ⏰ Time: {test_data['receivedAt']}")
        print("   " + "="*50)
        
        return True
    except Exception as e:
        print(f"❌ Erreur lors de l'insertion: {e}")
        return False
    finally:
        client.close()

def insert_multiple_documents(count=5, delay=2):
    """Insère plusieurs documents avec un délai entre chaque"""
    print(f"🚀 Insertion de {count} documents de test...")
    
    for i in range(count):
        print(f"\n📄 Insertion du document {i+1}/{count}...")
        success = insert_single_document()
        
        if not success:
            print("❌ Arrêt de l'insertion")
            break
            
        if i < count - 1:  # Ne pas attendre après le dernier document
            print(f"⏳ Attente de {delay} secondes...")
            time.sleep(delay)
    
    print(f"\n🎯 Insertion terminée!")

def check_existing_data():
    """Vérifie les données existantes dans la collection"""
    client = MongoClient(MONGODB_URI)
    db = client.tracksecure
    collection = db.sensor_data
    
    count = collection.count_documents({})
    print(f"📊 Nombre total de documents dans 'sensor_data': {count}")
    
    if count > 0:
        print("🔍 Dernier document:")
        last_doc = collection.find().sort("_id", -1).limit(1)[0]
        print(f"   Device ID: {last_doc.get('deviceId')}")
        print(f"   Dernière mise à jour: {last_doc.get('receivedAt')}")
    
    client.close()
    return count

def main():
    print("🔧 Script d'insertion de données de test MongoDB")
    print("=" * 50)
    
    # Vérifier les données existantes
    existing_count = check_existing_data()
    
    if existing_count > 0:
        print(f"\n💡 Il y a déjà {existing_count} documents dans la collection.")
        choice = input("Voulez-vous ajouter plus de données? (o/n): ").lower()
        if choice != 'o':
            print("👋 Au revoir!")
            return
    
    # Demander le nombre de documents à insérer
    try:
        count = int(input("\nCombien de documents voulez-vous insérer? (défaut: 3): ") or "3")
        delay = int(input("Délai entre chaque insertion en secondes? (défaut: 2): ") or "2")
    except ValueError:
        count = 3
        delay = 2
        print("⚠️  Utilisation des valeurs par défaut: 3 documents, délai de 2 secondes")
    
    # Insérer les documents
    insert_multiple_documents(count, delay)
    
    # Vérifier le résultat final
    print("\n" + "=" * 50)
    check_existing_data()

if __name__ == "__main__":
    main()