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

### Circuit Diagram 
Below is the circuit wiring for the IoT Parcel Tracking Device using **ESP32**, **GPS module**, and **DHT11 temperature sensor**.

![Circuit Diagram](/docs/assets/circuit0.png)

### Real-life Circuit 

![Real-life Circuit](/docs/assets/circuit1.jpg)
