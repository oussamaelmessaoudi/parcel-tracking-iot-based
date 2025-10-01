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
