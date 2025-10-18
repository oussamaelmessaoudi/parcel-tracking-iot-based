#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <PubSubClient.h>
#include <DHT.h>

DHT dht(D1, DHT11);

const char* ssid = "1WIFI";
const char* password = "1234432111";
const char* mqttServer = "broker.hivemq.com";
const int mqttPort = 1883;
const char* topic = "sensor/dht11";

WiFiClient espClient;
PubSubClient client(espClient);

void setup() {
  Serial.begin(115200);
  WiFi.begin(ssid, password);
  dht.begin();

  Serial.print("Connecting to WiFi");
  while(WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }

  client.setServer(mqttServer,mqttPort);
  Serial.println("\nWiFi connected, MQTT ready");
}

void loop() {

  if(!client.connected()){
    client.connect("ESP8266Client");
  }

  float temp = dht.readTemperature();
  float humd = dht.readHumidity();

  if (!isnan(temp) && !isnan(humd)) {
    String payload = "{";
    payload += "\"temperature\":" + String(temp) + ",";
    payload += "\"humidity\":" + String(humd);
    payload += "}";

    client.publish(topic,payload.c_str());
    Serial.println("Published : "+payload);
  }

  client.loop();
  delay(10000); // Send every 10 seconds
}
