#include <ESP8266WiFi.h>
#include <WiFiClientSecureBearSSL.h>
#include <PubSubClient.h>

// --- WiFi credentials ---
const char* ssid = "TP-Link_E70F";
const char* password = "32455847";

// --- MQTT broker info ---
const char* mqttServer = "192.168.100.253";  // your PC IP
const int mqttPort = 8883;                   // TLS port
const char* mqttUser = "oussama";       // from passwd file
const char* mqttPassword = "123456";

// --- CA certificate (same one used to sign server.crt) ---
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
BearSSL::WiFiClientSecure wifiClient;
PubSubClient client(wifiClient);

void setup() {
  Serial.begin(115200);
  delay(1000);
  Serial.println();
  Serial.println("Connecting to WiFi...");

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println();
  Serial.print("Connected to WiFi, IP: ");
  Serial.println(WiFi.localIP());

  // Attach CA certificate to validate server
  // wifiClient.setTrustAnchors(new BearSSL::X509List(ca_cert));
  wifiClient.setInsecure();

  client.setServer(mqttServer, mqttPort);

  Serial.println("Connecting to MQTT broker over TLS...");
  if (client.connect("ESP8266Client", mqttUser, mqttPassword)) {
    Serial.println("✅ MQTT connected securely!");
  } else {
    Serial.print("❌ Connection failed, rc=");
    Serial.println(client.state());
  }
}

void loop() {
  // nothing to do, just stay idle
}
