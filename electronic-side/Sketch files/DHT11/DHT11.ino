#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <PubSubClient.h>
#include <DHT.h>
#include <SoftwareSerial.h>
#include <TinyGPS++.h>

// TinyGPSPlus gps;
// SoftwareSerial gpsSerial(D7,D8);
DHT dht(D3, DHT11);

const char* ssid = "TP-Link_E70F";
const char* password = "32455847";
const char* mqttServer = "192.168.100.253";
const int mqttPort = 1883;
const char* topic = "sensor/dht11";

WiFiClient espClient;
PubSubClient client(espClient);

void setup() {
  Serial.begin(115200);
  // gpsSerial.begin(9600);
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

  // while(gpsSerial.available() > 0){
  //   gps.encode(gpsSerial.read());
  // }
  float temp = dht.readTemperature();
  float humd = dht.readHumidity();

  if (!isnan(temp) && !isnan(humd)) {
    String payload = "{";
    payload += "\"temperature\":" + String(temp) + ",";
    payload += "\"humidity\":" + String(humd);
    // payload += "\"latitude\":" + String(gps.location.lat(),6) + ",";
    // // payload += "\"longitude\":" + String(gps.location.lng(),6);
    payload += "}";

    client.publish(topic,payload.c_str());
    Serial.println("Published : "+payload);
  }else{
    Serial.println("Waiting for valid sensor or GPS data ...");
  }

  client.loop();
  delay(2000); // Send every 10 seconds
}
