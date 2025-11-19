#include <SoftwareSerial.h>
#include <TinyGPS++.h>

#define RXD2 D1   // GPS TX connects here
#define TXD2 D2   // GPS RX connects here
#define GPS_BAUD 9600

SoftwareSerial gpsSerial(RXD2, TXD2);
TinyGPSPlus gps;

void setup() {
  Serial.begin(115200);
  gpsSerial.begin(GPS_BAUD);
  Serial.println("Waiting for GPS data...");
}

void loop() {
  while (gpsSerial.available() > 0) {
    gps.encode(gpsSerial.read());
  }

  if (gps.location.isValid()) {
    Serial.print("Latitude: ");
    Serial.println(gps.location.lat(), 6);

    Serial.print("Longitude: ");
    Serial.println(gps.location.lng(), 6);

    Serial.println("--------------------------");
  }
}
