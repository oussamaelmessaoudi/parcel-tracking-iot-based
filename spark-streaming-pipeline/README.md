# Parcel Tracking IoT (kafka+spark) – Setup Guide

## Steps Completed So Far

### 1️⃣ Install Spark and Java
- Java 11 installed and configured (`JAVA_HOME` set Environment variable).
- Spark installed locally (version 3.5.7)(`SPARK_HOME` set Environment variable): https://spark.apache.org/downloads.html
- Verification:
  ```bash
  spark-submit --version
  java -version

### 2️⃣ Prepare Hadoop for Windows
- Download Hadoop : https://hadoop.apache.org/release/3.2.1.html
- Download winutils.exe for Hadoop :https://github.com/cdarlint/winutils/blob/master/hadoop-3.3.6/bin/winutils.exe
- Download hadoop.dll : https://github.com/cdarlint/winutils/blob/master/hadoop-3.3.6/bin/hadoop.dll
- Add winutils.exe and hadoop.dll to C:\hadoop\hadoop-3.2.1\bin
- Add environment variables: (`HADOOP_HOME` set Environment variable)
  ```bash
    set HADOOP_HOME=C:\hadoop\hadoop-3.2.1
    set PATH=%PATH%;%HADOOP_HOME%\bin
    
### 3️⃣ Create a Python Virtual Environment
- Create and activate:
  ```bash
    python -m venv venv
    venv\Scripts\activate
- Install dependencies:
  ```bash
    pip install -r requirements.txt

### 4️⃣ Configure Kafka and Zookeeper (Docker)
- installer Docker compose
- Start the services: 
  ```bash
    docker-compose up -d
    docker ps


