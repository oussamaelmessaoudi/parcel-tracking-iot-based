import json
from kafka import KafkaConsumer

# Create a consumer
# It also connects to 'localhost:9092'
consumer = KafkaConsumer(
    'gps', 'temperature', 'battery',
    bootstrap_servers='localhost:9092',
    auto_offset_reset='earliest',
    consumer_timeout_ms=10000,
    value_deserializer=lambda m: json.loads(m.decode('utf-8'))
)

print("Consumer started. Listening for messages on 'gps', 'temperature', and 'battery' topics...")

try:
    for message in consumer:
        print("\n--- New Message Received ---")
        print(f"Topic: {message.topic}")
        print(f"Partition: {message.partition}")
        print(f"Offset: {message.offset}")
        print(f"Key: {message.key}")
        print(f"Value (JSON): {message.value}")

except Exception as e:
    print(f"An error occurred: {e}")

finally:
    print("\nStopping consumer (timeout reached or script stopped).")
    consumer.close()