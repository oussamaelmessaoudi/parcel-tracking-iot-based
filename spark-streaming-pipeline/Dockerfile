FROM jupyter/pyspark-notebook:latest

USER root

# Installer dépendances Linux nécessaires (curl pour télécharger JARs, netcat pour wait-for-it)
RUN apt-get update && \
    apt-get install -y curl netcat && \
    rm -rf /var/lib/apt/lists/*

RUN pip install --no-cache-dir kafka-python pyspark
WORKDIR /app
RUN mkdir -p /app/models /app/checkpoints /app/jars

# Redonner les droits à l'utilisateur jovyan
USER $NB_UID
