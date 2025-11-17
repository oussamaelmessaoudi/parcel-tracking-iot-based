import json
from kafka import KafkaProducer
import time
import uuid

producer = KafkaProducer(
    bootstrap_servers='localhost:9092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

while True:
    record = {
        "package_id": str(uuid.uuid4()),  # UNIQUE ID each time
        "sensor_id": "ESP8266_SENSOR",
        "gps_latitude": 35.6895,
        "gps_longitude": 139.6917,
        "speed": 50.0,
        "temperature": 25.5,
        "humidity": 55.0,
        "battery_level": 95.0
    }
    producer.send('parcel_topic', record)
    print(f"Sent: {record}")
    time.sleep(2)
