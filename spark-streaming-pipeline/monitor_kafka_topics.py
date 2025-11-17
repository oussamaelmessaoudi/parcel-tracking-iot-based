from kafka import KafkaConsumer
import json

def monitor_kafka_messages():
    print("🎯 Monitoring des messages Kafka en temps réel...")
    
    topics = ['gps', 'temperature', 'battery']
    
    for topic in topics:
        print(f"\n📡 Checking topic: {topic}")
        try:
            consumer = KafkaConsumer(
                topic,
                bootstrap_servers=['localhost:9092'],
                auto_offset_reset='earliest',
                enable_auto_commit=True,
                group_id='debug_group'
            )
            
            message_count = 0
            for message in consumer:
                if message_count >= 5:  # Lire seulement 5 messages par topic
                    break
                    
                print(f"📨 Topic: {topic}, Message: {message.value.decode('utf-8')}")
                message_count += 1
                
            consumer.close()
            print(f"✅ {message_count} messages trouvés dans '{topic}'")
            
        except Exception as e:
            print(f"❌ Erreur avec le topic '{topic}': {str(e)}")

if __name__ == "__main__":
    monitor_kafka_messages()