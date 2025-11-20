FROM jupyter/pyspark-notebook:latest

USER root

# Installer dépendances Linux nécessaires (curl pour télécharger JARs, netcat pour l'attente Kafka,
# dos2unix pour corriger les fins de ligne de Windows)
RUN apt-get update && \
    apt-get install -y curl netcat dos2unix && \
    rm -rf /var/lib/apt/lists/*

# Installer les dépendances Python requises par le script de streaming
# (kafka-python est nécessaire pour le producer, mais aussi utile pour le consumer)
RUN pip install --no-cache-dir kafka-python

WORKDIR /app

# Créer les répertoires nécessaires pour les montages de volume
RUN mkdir -p /app/models /app/checkpoints /app/jars

# Laisser les permissions à l'utilisateur jovyan
USER $NB_UID