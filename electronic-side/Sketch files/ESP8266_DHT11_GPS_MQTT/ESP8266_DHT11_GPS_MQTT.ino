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
const int mqttPort = 8883; // TLS port
const char* mqttUser = "oussama";
const char* mqttPassword = "123456";
const char* topic_dht = "sensor/dht11";
const char* topic_gps = "sensor/gps";

// --- TLS certificate (CA) ---
const char ca_cert[] PROGMEM = R"EOF(
-----BEGIN CERTIFICATE-----
MIIDHjCCAgagAwIBAgIUcTcn7Oa8CT8xZ+Q0xu4nIFgtQ9EwDQYJKoZIhvcNAQEL
BQAwGDEWMBQGA1UEAwwNTXlNb3NxdWl0dG9DQTAeFw0yNTEwMjYxODU0MTBaFw0y
NjEwMjYxODU0MTBaMBoxGDAWBgNVBAMMDzE5Mi4xNjguMTAwLjI1MzCCASIwDQYJ
KoZIhvcNAQEBBQADggEPADCCAQoCggEBAML7qg9uZd3EZWojkCF4HKUQdSdDQ4UG
csA8vm78721n9CHiX4tPFriJcGK7TWDhvQA2DqVlF2nFPtm52olNh6r2ce3/QzVm
GxW5RH+jP4ebOJ1p9okHmWau1Vi2WytwdBZp77GLk3wEVWBDuORNIYMGA/5yAZYW
ltqPuUoA1BHUnpKRTlse6zxNvK9QjX6b2HVtfSavHjljThxNwblhOUJNA9Ym1xlb
dD8G8P3u3La5Cxzkl95IoTg3j8DQIKDYfYAK/0w6hGPjGFHPcOApbIP+3DDXP/f7
5e1Sd+H39CCgwEBZk+xENDBWqbcO4qukOoSmrMsJ8IkbEmOSzzFX0UECAwEAAaNe
MFwwGgYDVR0RBBMwEYIJbG9jYWxob3N0hwTAqGT9MB0GA1UdDgQWBBTZZxU4uCMF
+21HA4bDBPtZ0/1vSTAfBgNVHSMEGDAWgBTYQJhsyv0Mtb5Zlr3yIAziDzSuUDAN
BgkqhkiG9w0BAQsFAAOCAQEAFzClN47RFHZgmyZh+DMYzEV8jcOGQouJr3p9dQb0
HL5wdsBqCagVCQs13W+DrWIR9n+knj5MxPLuvY5NoF2BmRD4w87x38OsFlvRnn2E
+WiqOCAlutbuxz8qiChUkB6MCt6bMk5j/4b+9IUJooKfYIeRz1FVwsMKSeNT3BRi
SPhYXo/0Hn96OXHWU3k7SCsY1rrBB2ENbyxJ+QSMNI+vfwhPhMbI7ykJ5LmJmIqF
3ledmAcHQC6BlWzkjequ7DFQcISf6brpYShJODfjVV/maFeR1EHJAdxyZXHOPDoF
beOfBb9m/AhSs9k7xuYQL+rjr+trKWS3Tei5ReYcZboa7Q==
-----END CERTIFICATE-----
)EOF";

// --- Clients ---
BearSSL::WiFiClientSecure espClient;
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
  espClient.setInsecure();

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