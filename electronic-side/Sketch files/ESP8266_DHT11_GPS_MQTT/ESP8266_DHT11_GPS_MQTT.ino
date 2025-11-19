#include <ESP8266WiFi.h>
#include <WiFiClientSecureBearSSL.h>
#include <PubSubClient.h>
#include <DHT.h>
#include <SoftwareSerial.h>
#include <TinyGPS++.h>

// --- Pins ---
#define DHTPIN D3
#define DHTTYPE DHT11
#define RXD2 D2
#define TXD2 D1
#define GPS_BAUD 9600

// --- WiFi ---
const char* ssid = "TP-Link_E70F";
const char* password = "32455847";

// --- MQTT ---
const char* mqttServer = "192.168.100.253";
const int mqttPort = 1883; //8883; // TLS port
const char* mqttUser = "oussama";
const char* mqttPassword = "123456";
const char* topic_dht = "sensor/dht11";
const char* topic_gps = "sensor/gps";

// --- TLS certificate (CA) ---
const char ca_cert[] PROGMEM = R"EOF(
-----BEGIN CERTIFICATE-----
MIIC2zCCAcOgAwIBAgIUf3KIEBLgf0gl3dP94cGyf7Is0JUwDQYJKoZIhvcNAQEL
BQAwFjEUMBIGA1UEAwwLbW9zcXVpdHRvQ0EwHhcNMjUxMTAyMTMwNzQ0WhcNMjYx
MTAyMTMwNzQ0WjAWMRQwEgYDVQQDDAttb3NxdWl0dG9DQTCCASIwDQYJKoZIhvcN
AQEBBQADggEPADCCAQoCggEBAJe3zVD8eK9Jl+qP77BnZ/+ykIaU2s0qJyqO7k2+
3KwowDUCtQrUgbISABD80q0Fgh1fZk2KwWQosMOncusbmtqelzVecLMLshUhWjG6
/b9Kqun9DMM3HWgtwZqhlPOTcc8a1z377lmZlV804gzZKDc/0FbnGgeSAkEO0h9b
DRAmHZyqvHPd4n3mVxO4z0dcMAiFGn45MKTsUob52tK2++u09xe8of/M73pRapTj
PJ48OZ6aegJJGCGaP5vudc9QV/gzDNkmspLnojhX3pRKawclxyQPVIyXEx3Aoi3z
YPHAzuNWNJ2UUu6GeCaSuUEBuDocfLyR3Wiq1pnP+rtsHOUCAwEAAaMhMB8wHQYD
VR0OBBYEFEZa5ygqR1RjSGY8hsjRmaTNxr0+MA0GCSqGSIb3DQEBCwUAA4IBAQBH
lduV+00M+kyfxyUdOlJRBRu0CWGvJQjNOAWVwdfYOcW76xhzFc30tQVeWtiTXZiL
kzgHIA956648OajfaBI8cGMIbMuXu3mNRJb5cQW2z8kdyhs1DHSabvAx+YmJWRQI
0MXvHinx7ne4zJCYpjwe05qeAntPtKXMNBd1cc3MQf6eq0f/ErPaVQPNEzVw9d5r
z76K5ZkvxB0MqM7kz98B0ZzQr62+ggxv8L9EhgE8U0vsuCdpsyU6eeGf3F1pT0/m
YJhRxFSnslIu0lb0PgbK8pvdx6cggCwM/eLONfViudPJ2OYRnJZ/Sz+eqRPjgz2e
VyIHH5aG/rIVrukSBr5y
-----END CERTIFICATE-----
)EOF";

// --- Clients ---
//BearSSL::WiFiClientSecure espClient;
WiFiClient espClient;
PubSubClient mqttClient(espClient);
DHT dht(DHTPIN, DHTTYPE);
SoftwareSerial gpsSerial(RXD2, TXD2);
TinyGPSPlus gps;

unsigned long lastPublish = 0;
const unsigned long publishInterval = 5000;

void setup() {
  Serial.begin(115200);
  gpsSerial.begin(GPS_BAUD);
  dht.begin();

  // Connect WiFi
  Serial.print("Connecting to WiFi");
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected, IP: " + WiFi.localIP().toString());

  // TLS certificate validation
  // Option 1: Use proper certificate validation (recommended for production)
  // espClient.setTrustAnchors(new BearSSL::X509List(ca_cert));
  
  // Option 2: Skip certificate validation (for testing only)
  //espClient.setInsecure();

  mqttClient.setServer(mqttServer, mqttPort);
  mqttClient.setKeepAlive(60);
  mqttClient.setSocketTimeout(30);

  Serial.println("MQTT client ready with TLS");
}

void reconnectMQTT() {
  while (!mqttClient.connected()) {
    Serial.print("Connecting to MQTT...");
    if (mqttClient.connect("ESP8266Client", mqttUser, mqttPassword)) {
      Serial.println("connected");
      mqttClient.publish("test/topic", "ESP8266 connected");
    } else {
      Serial.print("failed, rc=");
      Serial.print(mqttClient.state());
      Serial.println(", retry in 2s");
      delay(2000);
    }
  }
}

void loop() {
  if (!mqttClient.connected()) {
    reconnectMQTT();
  }
  mqttClient.loop();

  while (gpsSerial.available() > 0) {
    gps.encode(gpsSerial.read());
  }

  unsigned long now = millis();
  if (now - lastPublish > publishInterval) {
    lastPublish = now;

    // Publish GPS
    if (gps.location.isValid() && gps.location.isUpdated()) {
      String payload_gps = "{";
      payload_gps += "\"latitude\":" + String(gps.location.lat(), 6) + ",";
      payload_gps += "\"longitude\":" + String(gps.location.lng(), 6) + ",";
      payload_gps += "\"satellites\":" + String(gps.satellites.value());
      payload_gps += "}";
      
      if (mqttClient.publish(topic_gps, payload_gps.c_str())) {
        Serial.println("GPS Published: " + payload_gps);
      } else {
        Serial.println("GPS Publish FAILED!");
      }
    } else {
      Serial.println("Waiting for valid GPS signal...");
    }

    // Publish DHT
    float temp = dht.readTemperature();
    float humd = dht.readHumidity();
    if (!isnan(temp) && !isnan(humd)) {
      String payload_dht = "{";
      payload_dht += "\"temperature\":" + String(temp, 2) + ",";
      payload_dht += "\"humidity\":" + String(humd, 2);
      payload_dht += "}";
      
      if (mqttClient.publish(topic_dht, payload_dht.c_str())) {
        Serial.println("DHT Published: " + payload_dht);
      } else {
        Serial.println("DHT Publish FAILED!");
      }
    } else {
      Serial.println("Waiting for valid DHT data...");
    }

    Serial.println("--------------------------");
  }
}