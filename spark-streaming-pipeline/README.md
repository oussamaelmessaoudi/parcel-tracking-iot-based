# 📦 Real-Time Parcel Tracking & Anomaly Detection System

## 📋 Project Overview

This project builds an end-to-end streaming data pipeline for logistics. It simulates IoT sensors (GPS, Temperature, Speed) attached to parcels moving across Morocco, streams this data into Apache Kafka, and processes it in real-time using Apache Spark Structured Streaming.

The system integrates pre-trained Machine Learning models to provide intelligent insights on the fly:

Delay Prediction: Uses a Random Forest Classifier to predict if a shipment will be delayed based on route and conditions.

Anomaly Detection: Uses K-Means Clustering to detect abnormal behavior (e.g., route deviation, sensor failure, unusual stops).

## 🏗️ Architecture

The entire stack is containerized using Docker:

- Zookeeper: Manages the Kafka cluster state.

- Kafka: Ingests high-throughput real-time sensor data from the producer.

- Producer (Python): Simulates truck fleets moving between major Moroccan cities (Tanger, Casablanca, Agadir, etc.), generating realistic GPS tracks and sensor readings.

- Spark Processor: A streaming application (consumer.py) that loads ML models, consumes Kafka data, performs feature engineering, and outputs predictions/alerts.

- Spark Notebook (Jupyter): An interactive environment to explore historical data and train the Machine Learning models.

## 📂 Project Structure

- docker-compose.yml - Orchestration of all services.

- producer.py - Generates synthetic "Morocco-aware" logistics data.

- consumer.py - The Spark Structured Streaming job with ML inference.

- model_training.ipynb - Jupyter Notebook for training Random Forest & KMeans models.

- data/Data_generator.py - Script to create the historical CSV dataset.

- data/ - Stores historical CSVs (e.g., morocco_full_dataset.csv).

- models/ - Shared volume where trained Spark ML models are saved.

## 🚀 Getting Started

### 1. Prerequisites

Docker Desktop installed and running.

Python 3.x installed locally (for data generation).

### 2. Start the Environment

Boot up the entire pipeline with a single command:
```bash
docker-compose up --build -d
```

### 3. Generate Historical Data (Crucial)

Before training the models, you must generate the synthetic dataset that simulates Moroccan logistics. This ensures the model learns the correct geography and "normal" behavior.

Run the generator script:

# Generates 'data/morocco_full_dataset.csv'
```bash
python data/Data_generator.py
```

### 4. Train the Models (First Run Only)

- Before the streaming processor can predict anything, it needs trained models.

- Open your browser and go to http://localhost:8888 (Jupyter Lab).

- Open model_training.ipynb.

- Ensure the dataset path matches the generated file (e.g., data/morocco_full_dataset.csv).

- Click Run All Cells.

- This will train the Random Forest and K-Means models.

- It saves the pipelines and scalers into the models/ directory.

### 5. Activate the Real-Time Stream

Once models are trained, ensure the Spark Processor loads them:
```bash
# Restart the processor to load the newly created models
docker-compose restart spark-processor
```

### 6. Monitor the Pipeline

You can watch the real-time processing in the terminal logs.

View Producer Logs (Sending data):
```bash
docker logs -f producer
```

Output: [Producer] pkg_001 (Route-Nord): Lat=34.0208, Lon=-6.8416, Speed=82.0

View Spark Processor Logs (Predictions & Anomalies):
```bash
docker logs -f spark-processor
```

Output:
```bash
-------------------------------------------
Batch: 15
-------------------------------------------
+----------+-----------------------+-----------+-----+---------------+--------+--------------------+
|package_id|event_time             |Temperature|Speed|Delay_Status   |IF_Alert|KMeans_Anomaly_Score|
+----------+-----------------------+-----------+-----+---------------+--------+--------------------+
|pkg_001   |2025-11-19 21:45:10.55 |25.5       |82.0 |ON_TIME        |NORMAL  |0.85421             |
|pkg_002   |2025-11-19 21:45:10.55 |38.2       |0.0  |DELAY_PREDICTED|ANOMALY |24.12455            |
+----------+-----------------------+-----------+-----+---------------+--------+--------------------+
```

### 🧠 Machine Learning Logic

Normal Behavior: A truck moving at ~80km/h on known coordinates (e.g., Highway A3) with standard temperature (~25°C) produces a low Anomaly Score (< 20).

Anomaly: If the producer simulates a breakdown (Speed=0) or extreme heat (Temp=45), the Anomaly Score spikes (> 20), triggering an ANOMALY alert.

Delay: The Random Forest classifier looks at the route and current conditions to predict ON_TIME or DELAY_PREDICTED.

### 🛠️ Troubleshooting

"Permission Denied" in Jupyter: The docker-compose.yml is configured to map permissions correctly for the jovyan user. If issues persist, check file ownership in the models/ folder.

High Anomaly Scores (~2000+): This means the streaming data does not match the training data distribution (e.g., training on US coordinates but streaming Morocco coordinates). Ensure you generated the Morocco dataset (Step 3) and retrained the models (Step 4).

Container Crashing: Ensure 4GB+ RAM is allocated to Docker, as Spark + Kafka can be memory-intensive.
