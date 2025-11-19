<<<<<<< HEAD
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
=======
# 🚚 Real-Time Parcel Tracking Pipeline (IoT + Kafka + Spark)

## 📋 Project Overview
This is an end-to-end streaming data pipeline project. It simulates IoT sensors (GPS, temperature, battery) sending data to Kafka. A Spark Streaming script (`process.py`) processes this data in real-time and stores the results in PostgreSQL. Finally, Grafana visualizes the parcel's location and status in real-time.

**Docker Compose** manages the entire environment to avoid native Windows setup issues.

---

## 🏗️ Architecture

This project runs 5 services inside Docker:

1. **Zookeeper**: Coordinates Kafka
2. **Kafka**: Message broker for sensor data
3. **PostgreSQL**: Stores processed data
4. **Spark**: Processes streams with `process.py`
5. **Grafana**: Visualizes results

---

## ✅ Prerequisites
>>>>>>> 65b414ae64dcf8697ad125c249e20560e4fdea91

- Spark Processor: A streaming application (consumer.py) that loads ML models, consumes Kafka data, performs feature engineering, and outputs predictions/alerts.

<<<<<<< HEAD
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

## 2. Start the Environment

Boot up the entire pipeline with a single command:
```bash
docker-compose up --build -d
```

## 3. Generate Historical Data (Crucial)

Before training the models, you must generate the synthetic dataset that simulates Moroccan logistics. This ensures the model learns the correct geography and "normal" behavior.

Run the generator script:
```bash
Generates 'data/morocco_full_dataset.csv'
python data/Data_generator.py
=======
All Spark, Java, and Hadoop components run *inside* containers.

---

## 📁 Project Files

* `docker-compose.yml` - Complete environment blueprint
* `process.py` - Spark streaming processor
* `producer.py` - IoT data simulator
* `requirements.txt` - Python dependencies

---

## 🚀 Startup Guide

### 1️⃣ Prepare Environment (One-Time)
```bash
cd your-project-folder
python -m venv venv
.\venv\Scripts\activate
pip install -r requirements.txt
```

### 2️⃣ Launch Pipeline
```bash
docker-compose up
>>>>>>> 65b414ae64dcf8697ad125c249e20560e4fdea91
```
Let this terminal run. Services start automatically.

<<<<<<< HEAD
### 4. Train the Models (First Run Only)

Before the streaming processor can predict anything, it needs trained models.

- Open your browser and go to http://localhost:8888 (Jupyter Lab).

- Open model_training.ipynb.

- Ensure the dataset path matches the generated file (e.g., data/morocco_full_dataset.csv).

- Click Run All Cells.

- This will train the Random Forest and K-Means models.

- It saves the pipelines and scalers into the models/ directory.

### 5. Activate the Real-Time Stream

Once models are trained, ensure the Spark Processor loads them:

- Restart the processor to load the newly created models
```bash
docker-compose restart spark-processor
```

### 6. Monitor the Pipeline

- You can watch the real-time processing in the terminal logs.

- View Producer Logs (Sending data):
```bash
docker logs -f producer
```

- Output: [Producer] pkg_001 (Route-Nord): Lat=34.0208, Lon=-6.8416, Speed=82.0

- View Spark Processor Logs (Predictions & Anomalies):
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

## 🧠 Machine Learning Logic

- Normal Behavior: A truck moving at ~80km/h on known coordinates (e.g., Highway A3) with standard temperature (~25°C) produces a low Anomaly Score (< 20).

- Anomaly: If the producer simulates a breakdown (Speed=0) or extreme heat (Temp=45), the Anomaly Score spikes (> 20), triggering an ANOMALY alert.

- Delay: The Random Forest classifier looks at the route and current conditions to predict ON_TIME or DELAY_PREDICTED.

## 🛠️ Troubleshooting

- "Permission Denied" in Jupyter: The docker-compose.yml is configured to map permissions correctly for the jovyan user. If issues persist, check file ownership in the models/ folder.

- High Anomaly Scores (~2000+): This means the streaming data does not match the training data distribution (e.g., training on US coordinates but streaming Morocco coordinates). Ensure you generated the Morocco dataset (Step 3) and retrained the models (Step 4).

- Container Crashing: Ensure 4GB+ RAM is allocated to Docker, as Spark + Kafka can be memory-intensive.
=======
### 3️⃣ Create Kafka Topics (One-Time)
```bash
docker exec -it kafka bash
kafka-topics --create --topic gps --bootstrap-server kafka:29092
kafka-topics --create --topic temperature --bootstrap-server kafka:29092  
kafka-topics --create --topic battery --bootstrap-server kafka:29092
exit
```

### 4️⃣ Create PostgreSQL Table (One-Time)
```bash
docker exec -it postgres psql -U sparkuser -d parcel_db
```
```sql
CREATE TABLE parcel_predictions (
    package_id VARCHAR(255),
    event_time TIMESTAMP,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    temperature DOUBLE PRECISION, 
    battery_level DOUBLE PRECISION,
    delay_prediction DOUBLE PRECISION,
    traj_cluster INTEGER,
    anomaly_prediction DOUBLE PRECISION
);
```
Type `\q` to exit.

### 5️⃣ Restart Spark
```bash
docker-compose restart spark-processor
```

---

## 🧪 Testing & Visualization

### 6️⃣ Send Test Data
```bash
.\venv\Scripts\activate
python producer.py
```

### 7️⃣ Check Results
* **Spark Logs**: Look for `--- Writing Batch X ---` in docker-compose terminal
* **Database**:
```bash
docker exec -it postgres psql -U sparkuser -d parcel_db -c "SELECT * FROM parcel_predictions;"
```

### 8️⃣ Visualize in Grafana
1. Go to **http://localhost:3000**
2. Login: `admin` / `admin`
3. **Add Data Source**:
    - Type: PostgreSQL
    - Host: `postgres`
    - Database: `parcel_db`
    - User: `sparkuser`
    - Password: `sparkpassword`
    - SSL: `disable`
4. **Build Dashboard** with Geomap and Time series panels
5. Set auto-refresh to **5 seconds**

---

## 🤖 Enhanced with Machine Learning
The system now provides intelligent predictions:
- **Delay Prediction**: Risk of delivery delays
- **Anomaly Detection**: Unusual patterns in sensor data
- **Route Clustering**: Movement pattern analysis

---

## 🛠️ Troubleshooting
Use `consumer.py` to debug Kafka messages:
```bash
python consumer.py
```

Check Spark logs:
```bash
docker logs spark-processor
```

**Expected Success Signs:**
- Producer: "Sent data for pkg_001..."
- Spark: "Batch X written (X rows)"
- PostgreSQL: Growing predictions table
- Grafana: Live maps with parcel tracking
>>>>>>> 65b414ae64dcf8697ad125c249e20560e4fdea91
