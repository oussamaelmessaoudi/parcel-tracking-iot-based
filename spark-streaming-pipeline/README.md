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

* [**Docker Desktop**](https://www.docker.com/products/docker-desktop/)
* [**Python 3+**](https://www.python.org/)

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
```
Let this terminal run. Services start automatically.

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
