FROM bitnami/spark:3.5.0

USER root

# Installer les dépendances système
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Installer les connecteurs Python
RUN pip3 install pymongo kafka-python

# Télécharger les JARs nécessaires
RUN curl -L -o /opt/bitnami/spark/jars/mongo-spark-connector.jar \
    https://repo1.maven.org/maven2/org/mongodb/spark/mongo-spark-connector_2.12/3.0.1/mongo-spark-connector_2.12-3.0.1.jar

RUN curl -L -o /opt/bitnami/spark/jars/mongo-java-driver.jar \
    https://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/3.12.11/mongo-java-driver-3.12.11.jar

RUN curl -L -o /opt/bitnami/spark/jars/spark-sql-kafka.jar \
    https://repo1.maven.org/maven2/org/apache/spark/spark-sql-kafka-0-10_2.12/3.5.0/spark-sql-kafka-0-10_2.12-3.5.0.jar

# Copier votre application
COPY process.py /app/process.py

# Créer les dossiers nécessaires
RUN mkdir -p /app/checkpoints/mongodb_sink_ml

WORKDIR /app

# Donner les permissions
RUN chown -R 1001:1001 /app