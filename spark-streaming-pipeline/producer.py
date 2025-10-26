# producer_fixed.py
import json
from kafka import KafkaProducer
import time
import random

producer = KafkaProducer(
    bootstrap_servers='localhost:9092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

print("Live producer started. Sending to 3 topics (gps, temperature, battery)...")
print("Press Ctrl+C to stop.")

# Starting data
lat = 34.0208
lon = -6.8416
temp = 25.0
batt = 95.0  # Changed to percentage
speed = 60.0
humidity = 55.0
package_id = "pkg_001"

try:
    while True:
        # Simulate changes
        lat += random.uniform(-0.0005, 0.0005)
        lon += random.uniform(-0.0005, 0.0005)
        temp += random.uniform(-0.5, 0.5)
        batt -= 0.01  # Slow battery drain
        if batt < 0: batt = 0
        speed += random.uniform(-3, 3)
        if speed < 0: speed = 0

        current_time = time.strftime('%Y-%m-%d %H:%M:%S')

        # Create 3 separate messages with CORRECT field names matching Spark schema
        gps_data = {
            "package_id": package_id,  # Changed from "Route ID"
            "latitude": lat,           # Changed from "GPS Latitude"
            "longitude": lon,          # Changed from "GPS Longitude"
            "Speed": speed,
            "timestamp": current_time
        }

        temp_data = {
            "package_id": package_id,  # Changed from "Route ID"
            "temperature": temp,
            "Humidity": humidity,
            "timestamp": current_time
        }

        batt_data = {
            "package_id": package_id,  # Changed from "Route ID"
            "battery_level": batt,
            "battery_status": "healthy" if batt > 20 else "low",
            "timestamp": current_time
        }

        # Send to the 3 topics
        producer.send('gps', value=gps_data)
        producer.send('temperature', value=temp_data)
        producer.send('battery', value=batt_data)
        producer.flush()

        print(f"Sent data for {package_id} (lat: {lat:.4f}, temp: {temp:.1f}°C, battery: {batt:.1f}%)")

        time.sleep(5)

except KeyboardInterrupt:
    print("\nStopping producer.")
finally:
    producer.close()