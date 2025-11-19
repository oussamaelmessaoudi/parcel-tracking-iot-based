#!/bin/bash

# Script pour attendre que Kafka soit prêt et créer les topics nécessaires.

KAFKA_BOOTSTRAP_SERVERS="kafka:29092"

echo "Checking Kafka availability..."

# Boucle d'attente robuste pour s'assurer que Kafka est prêt
for i in {1..40}; do
  # Tente de lister les topics (commande qui échoue si Kafka n'est pas prêt)
  kafka-topics --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --list > /dev/null 2>&1
  if [ $? -eq 0 ]; then
    echo "Kafka is ready after $i seconds."
    break
  fi
  sleep 1
done

if [ $i -eq 40 ]; then
  echo "Error: Kafka did not become available within 40 seconds."
  exit 1
fi

echo "Creating necessary topics (gps, temperature, anomalies)..."
# Utilisation de '|| true' pour que le script ne s'arrête pas si le topic existe déjà
kafka-topics --create --topic gps --partitions 1 --replication-factor 1 --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS || true
kafka-topics --create --topic temperature --partitions 1 --replication-factor 1 --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS || true
kafka-topics --create --topic anomalies --partitions 1 --replication-factor 1 --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS || true

echo "Topic creation attempt finished."

# Empêche le conteneur de s'arrêter
tail -f /dev/null