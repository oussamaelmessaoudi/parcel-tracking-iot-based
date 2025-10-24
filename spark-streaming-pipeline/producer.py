import json
from kafka import KafkaProducer
import time

# Create a producer
# We use 'localhost:9092' because this script is running on your
# Windows machine, which is connected to the Docker port 9092.
producer = KafkaProducer(
    bootstrap_servers='localhost:9092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8') # Serialize JSON to bytes
)

print("Producer started. Sending messages to all topics...")

# 1. Define GPS message
gps_data = {
    "package_id": "PKG-12345",
    "latitude": 34.020882,
    "longitude": -6.841650
}

# 2. Define Temperature message
temperature_data = {
    "package_id": "PKG-12345",
    "temperature": 25.5
}

# 3. Define Battery message
battery_data = {
    "package_id": "PKG-12345",
    "battery_level": 0.82  # 82%
}


try:
    # Send the GPS message
    future_gps = producer.send('gps', value=gps_data)
    record_gps = future_gps.get(timeout=10)
    print(f"Message sent to 'gps' topic! Offset: {record_gps.offset}")

    # Send the Temperature message
    future_temp = producer.send('temperature', value=temperature_data)
    record_temp = future_temp.get(timeout=10)
    print(f"Message sent to 'temperature' topic! Offset: {record_temp.offset}")

    # Send the Battery message
    future_batt = producer.send('battery', value=battery_data)
    record_batt = future_batt.get(timeout=10)
    print(f"Message sent to 'battery' topic! Offset: {record_batt.offset}")

    print("\nAll messages sent successfully!")

except Exception as e:
    print(f"Error sending message: {e}")

finally:
    # Flush and close the producer
    producer.flush()
    producer.close()