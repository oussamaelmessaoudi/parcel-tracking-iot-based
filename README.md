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
=======
![composant](/docs/assets/SystemComponent.jpg)

## Running the App with Docker

7awlo tdiro build l docker image bach mat7taj tcharji depends, java wla maven

```bash
# 1- Pull the image 
docker pull oussamaelmessaoudi/sensors-payload:latest
```

# 2- Build the image locally

ila makanch 3ndkom docker desktop, try to use WSL2 ubuntu (using this command on Powershell):
```bash
wsl -d ubuntu
sudo service docker start
cd "/mnt/.../parcel-tracking-iot-based/electronic-side/SensorsPayload"
docker build -t sensors-payload .
```

# 3- Run the container

```bash
docker run -p 1440:1440 sensors-payload

```
daba app mtlo9 w t9dro tdkhlo lih via : http://localhost:1440

# 4- Notes :
- Khass darori ykon 3ndkom Docker m installer wdayr lih run
- awal build atdiro aykhad quelques minutes 
- ila drto lih build, l'image t9der t3awd tkhdam biha bla building y3ni atmshi l step 3 direct

## Circuit Diagram 
Below is the circuit wiring for the IoT Parcel Tracking Device using **ESP32**, **GPS module**, and **DHT11 temperature sensor**.

![Circuit Diagram](/docs/assets/circuit0.png)

## Real-life Circuit 

![Real-life Circuit](/docs/assets/circuit1.jpg)
