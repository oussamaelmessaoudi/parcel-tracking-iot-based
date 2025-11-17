import json
import time
from kafka import KafkaProducer
from datetime import datetime

# Configuration
KAFKA_BOOTSTRAP_SERVERS = 'kafka:29092'

print("🚀 Démarrage du générateur de données de test")

producer = KafkaProducer(
    bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

counter = 0

while True:
    test_data = {
        "package_id": f"test-package-{counter:03d}",
        "latitude": 34.0208 + (counter * 0.001),
        "longitude": -6.8416 + (counter * 0.001),
        "speed": 50.0 + (counter % 30),
        "temperature": 25.0 + (counter % 10),
        "humidity": 55.0 + (counter % 20),
        "battery_level": 95.0 - (counter % 50),
        "battery_status": "healthy" if (95.0 - (counter % 50)) > 20 else "low",
        "timestamp": datetime.now().isoformat(),
        "sensor_id": f"test-device-{counter % 3:03d}"
    }
    
    try:
        producer.send("gps", test_data)
        producer.send("temperature", test_data) 
        producer.send("battery", test_data)
        producer.flush()
        print(f"✅ Données de test #{counter} envoyées à Kafka")
        counter += 1
    except Exception as e:
        print(f"❌ Erreur envoi: {e}")
    
    time.sleep(5)