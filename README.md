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
- tracksecure-backend (empty project to regroup all the microservices)
	- common (shared code like DTOs, constant .... in order to prevent duplication across all the services)
		-DTOs (alert,tracking even, device, shipment)
		-Contants (For shared constants usage)
		-Exception ( Handling exceptions)
		-Utils (helper classes and reusable across all services)
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

## Dockerizing the Spring Boot MQTT Rest App :
### Build the Docker image
In order to run the MqttRestApp locally, you need to run these commands
From the root of electronic-side/MqttRestApp

```bash
docker build -t mqtt-rest-app .
````

### Run the container 
```bash
docker run -p 8080:8080 mqtt-rest-app
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
