# Parcel Tracking IoT Project – Spark Streaming & ML Pipeline

## Overview

This project implements a **real-time parcel tracking system** using Spark Streaming and machine learning models.
It predicts **delivery delays** using a **Random Forest model** and detects **anomalies** using **KMeans clustering** on streaming data from IoT sensors.

### Features

* Random Forest model for delay prediction.
* KMeans clustering for anomaly detection.
* Spark Structured Streaming with Kafka.
* Real-time alerts on console and Kafka topic.
* Handles missing or null values in streaming data.

---

## Prerequisites

* Python 3.10+
* Virtual environment (`venv`)
* Docker & Docker Compose
* Apache Spark 3.5+
* Kafka cluster (can run locally via Docker)
* Required Python packages (listed in `requirements.txt`)

---

## Setup

1. **Clone the repository**:

```bash
git clone https://github.com/oussamaelmessaoudi/parcel-tracking-iot-based.git
cd spark-streaming-pipeline
```

2. **Create and activate virtual environment**:

```bash
python -m venv .venv
# Windows
.\.venv\Scripts\activate
# Linux/Mac
source .venv/bin/activate
```

3. **Install Python dependencies**:

```bash
pip install -r requirements.txt
```

---

## Dataset

The streaming pipeline uses a **generated CSV dataset**:

* File: `Data_generator.csv`
* Used by `producer.py` to simulate IoT parcel data.
* Columns include: `package_id`, `timestamp`, `gps_latitude`, `gps_longitude`, `temperature`, `humidity`, `speed`

> You can modify `Data_generator.csv` to simulate different scenarios.

---

## 1. Training ML Models

> Before starting the streaming pipeline, train the ML models and save them in `/models`.

1. Navigate to the training script folder:

```bash
cd scripts/ml
```

2. Train the Random Forest and KMeans models:

```bash
python train_model.py
```

3. Models will be saved to:

```
/models/rf_model
/models/kmeans_model
/models/scaler_anom
```

Ensure these paths match the configuration in `consumer.py`.

---

## 2. Starting Kafka (via Docker Compose)

```bash
docker-compose up -d
```

This starts:

* Zookeeper
* Kafka broker
* Spark Master & Worker nodes

Check logs:

```bash
docker-compose logs -f
```

---

## 3. Start the Producer (Simulated Parcel Data)

```bash
python producer.py
```

* Reads from `Data_generator.csv`.
* Sends parcel data to Kafka topic `parcel_data`.
* Matches `INPUT_TOPIC` in `consumer.py`.

---

## 4. Start the Consumer (Spark Streaming Pipeline)

```bash
python consumer.py
```

* Reads streaming data from Kafka.
* Performs feature engineering.
* Predicts delays (Random Forest).
* Detects anomalies (KMeans).
* Sends alerts to:

  * Console
  * Kafka topic `anomalies`

---

## 5. Viewing Output

* **Console**: Shows all parcels with `Delay_Status`, `IF_Alert`, and `KMeans_Anomaly_Score`.
* **Kafka**: Alerts sent to `anomalies` topic (can be consumed by another service).

---

## 6. Environment Variables (Optional)

| Variable            | Description                     | Default       |
| ------------------- | ------------------------------- | ------------- |
| `KAFKA_BOOTSTRAP`   | Kafka broker address            | `kafka:29092` |
| `ANOMALY_THRESHOLD` | Threshold for anomaly detection | `2500.0`      |

---

## 7. Notes / Best Practices

* Always train models before running the consumer.
* Clean Spark checkpoints if you restart streaming:

```bash
rm -rf checkpoints/*
```

* Handle CRLF warnings if using Git on Windows.
* Use Docker for a reproducible environment.
* Activate your Python virtual environment before running scripts.

