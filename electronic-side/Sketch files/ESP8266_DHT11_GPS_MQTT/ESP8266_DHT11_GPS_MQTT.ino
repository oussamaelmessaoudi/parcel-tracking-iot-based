#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <DHT.h>
#include <SoftwareSerial.h>
#include <TinyGPS++.h>

// Pin definitions
#define DHTPIN D3
#define DHTTYPE DHT11
#define RXD2 D1
#define TXD2 D2
#define GPS_BAUD 9600  

// WiFi and MQTT credentials
const char* ssid = "TP-Link_E70F";
const char* password = "32455847";
const char* mqttServer = "192.168.100.253";
const int mqttPort = 1883;
const char* topic_dht = "sensor/dht11";
const char* topic_gps = "sensor/gps";

WiFiClient espClient;
PubSubClient client(espClient);
DHT dht(DHTPIN, DHTTYPE);
SoftwareSerial gpsSerial(RXD2, TXD2);
TinyGPSPlus gps;

unsigned long lastPublish = 0;
const unsigned long publishInterval = 5000; 

void setup() {
  Serial.begin(115200);
  gpsSerial.begin(GPS_BAUD);
  dht.begin();

  Serial.print("Connecting to WiFi");
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  client.setServer(mqttServer, mqttPort);
  Serial.println("\nWiFi connected, MQTT ready");
}

void reconnectMQTT() {
  while (!client.connected()) {
    Serial.print("Connecting to MQTT...");
    if (client.connect("ESP8266Client")) {
      Serial.println("connected");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 2 seconds");
      delay(2000);
    }
  }
}

void loop() {
  if (!client.connected()) {
    reconnectMQTT();
  }
  client.loop();

  // Read GPS data
  while (gpsSerial.available() > 0) {
    char c = gpsSerial.read();
    gps.encode(c);
    // Serial.write(c); // Debug: show raw NMEA
  }

  unsigned long now = millis();
  if (now - lastPublish > publishInterval) {
    lastPublish = now;

    // Publish GPS data if valid
    if (gps.location.isValid() && gps.location.isUpdated()) {
      String payload_gps = "{";
      payload_gps += "\"latitude\":" + String(gps.location.lat(), 6) + ",";
      payload_gps += "\"longitude\":" + String(gps.location.lng(), 6) + ",";
      payload_gps += "\"satellites\":" + String(gps.satellites.value());
      payload_gps += "}";
      client.publish(topic_gps, payload_gps.c_str());
      Serial.println("GPS Published: " + payload_gps);
    } else {
      Serial.println("Waiting for valid GPS signal...");
    }

    // Read DHT data
    float temp = dht.readTemperature();
    float humd = dht.readHumidity();

    if (!isnan(temp) && !isnan(humd)) {
      String payload_dht = "{";
      payload_dht += "\"temperature\":" + String(temp, 2) + ",";
      payload_dht += "\"humidity\":" + String(humd, 2);
      payload_dht += "}";
      client.publish(topic_dht, payload_dht.c_str());
      Serial.println("DHT Published: " + payload_dht);
    } else {
      Serial.println("Waiting for valid DHT data...");
    }

    Serial.println("--------------------------");
  }
}
