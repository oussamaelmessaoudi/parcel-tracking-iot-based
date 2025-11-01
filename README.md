# Parcel Tracking project

IoT-based project for real-time tracking parcel 

## Collaborators :
Oussama ELMESSAOUDI

Abdessamad ASKLOU

Noureddine AIT MOULAY BRAHIM

Yassine EL-ATIKI

Khaoula DAMANI

Fatima Zahra BOUKAMAR 

## System Components 

```
┌─────────────────────────────────────────────────────────────────┐
│                         IoT Devices Layer                        │
│  GPS Trackers | Temperature Sensors | Motion Sensors | RFID     │
└─────────────────────────┬───────────────────────────────────────┘
                          │ MQTT/CoAP over TLS
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                    IoT Gateway Service                           │
│  - Device Authentication (X.509 Certs/PSK)                      │
│  - Payload Decryption & Validation                              │
│  - Protocol Translation (MQTT/CoAP → Internal)                  │
└─────────────────────────┬───────────────────────────────────────┘
                          │ Kafka Events
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Apache Kafka                              │
│  Topics: tracking-events | alerts | device-status               │
└─────┬───────────────────┴───────────────────┬───────────────────┘
      │                                       │
      ▼                                       ▼
┌──────────────────────┐            ┌──────────────────────┐
│ Event Processor Svc  │            │  Alert Service       │
│ - Process events     │            │  - Detect anomalies  │
│ - Enrich data        │            │  - Send notifications│
│ - Update state       │            │  - Trigger actions   │
└──────────┬───────────┘            └──────────┬───────────┘
           │                                   │
           ▼                                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Core Tracking Service                         │
│  - REST APIs (Public + Internal)                                │
│  - gRPC APIs (Service-to-Service)                               │
│  - Business Logic                                               │
│  - Authentication & Authorization (OAuth2/JWT)                  │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Data Layer                                    │
│  PostgreSQL: Shipments, Devices, Events, Alerts                │
│  Redis: Caching, Real-time State                               │
└─────────────────────────────────────────────────────────────────┘
```
## Project Structure so far (will be updated in each merge)
```
tracksecure-backend/                  # Root project grouping all microservices
│
├── common/                           # Shared module to avoid code duplication
│   ├── dtos/                         # Data Transfer Objects (Alert, TrackingEvent, Device, Shipment)
│   ├── constants/                    # Shared constants
│   ├── exceptions/                   # Common exception handling
│   └── utils/                        # Helper and reusable utility classes
│
└── (other microservices to be added) # Example: sensors-payload, mqtt-rest-app, tracking-service...

```
# Circuit Diagram
## Circuit Wiring
![Circuit Diagram](/docs/assets/circuit0.png)
## Real life Circuit
![Real life Circuit](/docs/assets/circuit1.png)

# Dockerizing and Running Mosquitto Broker
## 1- Build and run Mosquitto Broker (with TLS)
From your electronic-sidde/Mosquitto directory (run this command in terminal):
```bash
mosquitto -c conf/mosquitto.conf -v
````
- Port 8883 is used for TLS
- Certiicates (ca.crt, server.crt, server.key) 3ndi, i didn't push them for security reasons, contact me if want them.

Once it runs, you should see logs like:

![Starting Mosquitto Broker](/docs/assets/mosquitto1.png)

You can skip this part, and run the full stack (Mosquitto, Zookeeper, Kafka, and Spring App) using the commands below.

## Dockerizing the stack :
Run these commands from the electronic-side directory, either directly on powershell terminal or docker desktop terminal :
```bash
docker compose down -v
docker compose build mqttrestapp --no-cache
docker compose up -d
````
This will start:
- Mosquitto broker first (TLS on port 8883) / should be on healthy status first
- Zookeeper (port 2181) / should be also healthy first to start Kafka
- Kafka Broker (port 9092)
- Spring Boot MQTT REST App (port 8080) / depends on Mosquitto and Kafka

## Check logs :

You can monitor logs in real-time:

```bash
docker compose logs -f mosquitto
docker compose logs -f mqttrestapp
````
When everything runs fine, you should see:
```bash
MQTT connected and subscriber to sensor topics
````

## Send static Data to Rest ( Test MQTT → Spring App) :
Ila makantch dayza alimentation dyal esp (no real data), you can publish static data into the topic :

```bash
mosquitto_pub -h localhost -p 8883 \
  -t "sensor/dht11" \
  -m '{"temperature":25.5,"humidity":60.0}' \
  -u oussama -P 123456 \
  --cafile ./Mosquitto/certs/ca.crt \
  --insecure
````

### Expected console output

## ESP8266 Firmware (MQTT over TLS)

Below is the important libraries used and pins 
```bash
#include <ESP8266WiFi.h> // For Wifi connectivity
#include <WiFiClientSecureBearSSL.h> // For secure TLS/SSL communication
#include <PubSubClient.h> // For MQTT client function like publishing or subscribing to a certain topics
#include "certs.h" // Contains CA, Client Cetificate and private key

D3 ==> DHT ping
D2 ==> RX pin of gps module
D1 ==> TX pin of gps module
````

Once connected, the ESP publishes data every 5 seconds to:
- sensor/dht11
- sensor/gps

You'll see in Mosquitto logs: 

![Mosquitto Broker logs](/docs/assets/mosquitto2.png)

The problem here is that the ESP always needs the alimentation to sends the data, so if you want to try the app out, hit me up and i'll supply it

# REST API from MqttRestApp

while the ESP sends the data, Mosquitto broker is listening and Spring Boot app is started

all you have to do here is head to the endpoint below
## Endpoint 

GET http://localhost:8080/sensor-data

or type the following command on the terminal :

```bash
curl "http://lcoalhost:8080/sensor-data"
````

## Example JSON response
![JSON Response](/docs/assets/json.png)
