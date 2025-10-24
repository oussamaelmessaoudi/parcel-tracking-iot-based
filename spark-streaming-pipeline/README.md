
# Real-Time Parcel Tracking Pipeline (IoT + Kafka + Spark)

This is an end-to-end streaming data pipeline project. It simulates IoT sensors (GPS, temperature, battery) sending data to Kafka. A Spark Streaming script (`process.py`) processes this data in real-time, joins the streams, and stores the results in a PostgreSQL database. Finally, a Grafana dashboard visualizes the parcel's location and status in real-time.

This entire environment is managed by **Docker Compose**. This method is used to avoid all native Windows setup issues and errors (like `HADOOP_HOME` or `NativeIO$Windows.access0`).

-----

## Architecture

This project is composed of 5 services that run inside Docker:

1.  **Zookeeper**: Required to coordinate Kafka.
2.  **Kafka** (`confluentinc/cp-kafka`): The message broker that ingests data from sensors.
3.  **PostgreSQL** (`postgres:14`): The database that stores the final, processed data.
4.  **Spark** (`jupyter/pyspark-notebook`): The "brain" that runs the `process.py` script to process the streams.
5.  **Grafana** (`grafana/grafana-oss`): The dashboard that reads from PostgreSQL and displays the results.

-----

## Prerequisites

To run this project, you only need two pieces of software on your computer:

* [**Docker Desktop**](https://www.docker.com/products/docker-desktop/)
* [**Python 3+**](https://www.python.org/)

All Spark, Java, and Hadoop components are handled *inside* the Docker containers.

-----

## Project Files

* `docker-compose.yml`: The blueprint for building the entire environment.
* `process.py`: The Spark streaming script that joins topics and saves to PostgreSQL.
* `producer_live.py`: (Test Script) A Python script to simulate an IoT sensor sending live data.
* `requirements.txt`: (Test Script) Python dependencies for the producer script.

-----

## Startup Guide (Step-by-Step)

### 1️⃣ Prepare Your Host Environment (One-Time Setup)

This step configures your Windows `venv` to run the test producer script.

```bash
# Navigate to your project folder
cd C:\Users\khaol\IdeaProjects\parcel-tracking-iot-based\spark-streaming-pipeline

# Create and activate a Python virtual environment
python -m venv venv
.\venv\Scripts\activate

# Install the Python Kafka library
pip install -r requirements.txt
```

### 2️⃣ Launch the Full Pipeline (Docker)

This is the main command. It starts all 5 services.

```bash
# Make sure Docker Desktop is running
# This will start Kafka, Spark, Postgres, and Grafana
docker-compose up
```

Let this terminal run. It will show you the logs from all services. The `spark` container will wait for `kafka` and `postgres` to be "healthy" before starting.

### 3️⃣ Create Kafka Topics (One-Time Setup)

The pipeline is running, but the topics do not exist yet.

1.  Open a **second, new** terminal.
2.  Enter the running Kafka container:
    ```bash
    docker exec -it kafka bash
    ```
3.  Inside the container, create your 3 topics:
    ```bash
    kafka-topics --create --topic gps --bootstrap-server kafka:29092
    kafka-topics --create --topic temperature --bootstrap-server kafka:29092
    kafka-topics --create --topic battery --bootstrap-server kafka:29092
    ```
4.  Type `exit` to leave the container.

### 4️⃣ Create PostgreSQL Table (One-Time Setup)

Spark needs a table to write its data into.

1.  In your **second** terminal, connect to the Postgres container:
    ```bash
    docker exec -it postgres psql -U sparkuser -d parcel_db
    ```
2.  Paste this SQL command to create the table that matches your `process.py` script:
    ```sql
    CREATE TABLE parcel_data (
        package_id VARCHAR(255),
        "timestamp" TIMESTAMP,
        latitude DOUBLE PRECISION,
        longitude DOUBLE PRECISION,
        temperature DOUBLE PRECISION,
        battery_level DOUBLE PRECISION
    );
    ```
3.  Type `\q` to exit `psql`.

### 5️⃣ Restart Spark

Now that the topics and table exist, restart the Spark service to apply the changes.

```bash
# In your second terminal
docker-compose restart spark
```

Your pipeline is now fully operational. In your **first** terminal (the `docker-compose up` one), you should see the Spark logs print `Starting stream to PostgreSQL... waiting for termination.`

-----

## How to Test and Visualize

### 6️⃣ Send Test Data

1.  Open a **third** terminal.
2.  Activate your virtual environment:
    ```bash
    .\venv\Scripts\activate
    ```
3.  Run the live producer script. This will start sending new data every 5 seconds.
    ```bash
    python producer.py
    ```

### 7️⃣ Check the Results

* **Check the Spark Logs (Terminal 1):** In your main `docker-compose` terminal, you will see the Spark logs react to the new data, showing:
  `--- Writing Batch 0 ---`
  `--- Batch 0 written successfully ---`
* **Check the Database (Terminal 2):** You can check the data directly in PostgreSQL:
  ```bash
  docker exec -it postgres psql -U sparkuser -d parcel_db
  SELECT * FROM parcel_data;
  ```

### 8️⃣ Visualize in Grafana

1.  Open your web browser and go to **`http://localhost:3000`**.
2.  Log in (default: `admin` / `admin`).
3.  **Connect the Data Source:**
    * Go to **Connections** \> **Add data source**.
    * Select **PostgreSQL**.
    * Fill in the details:
        * **Host:** `postgres`
        * **Database:** `parcel_db`
        * **User:** `sparkuser`
        * **Password:** `admin`
        * **SSL Mode:** `disable`
    * Click **"Save & Test"**.
4.  **Build Your Dashboard:**
    * Go to **Dashboards** \> **New**.
    * Add a **Geomap** panel to visualize `latitude` and `longitude` from your `parcel_data` table.
    * Add a **Time series** panel to track `temperature`.
    * Set the dashboard to auto-refresh every **5 seconds**.