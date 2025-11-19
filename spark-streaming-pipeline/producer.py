# producer_fixed.py
import json
from kafka import KafkaProducer
import time
import random
<<<<<<< HEAD
import os
import numpy as np
import math

# ==========================================
# CONFIGURATION KAFKA & VILLES
# ==========================================
bootstrap = os.environ.get("KAFKA_HOST", "kafka:29092")

# Mêmes coordonnées que le dataset d'entraînement
CITIES = {
    "Tanger":      {"lat": 35.7595, "lon": -5.8340},
    "Casablanca":  {"lat": 33.5731, "lon": -7.5898},
    "Rabat":       {"lat": 34.0208, "lon": -6.8416},
    "Marrakech":   {"lat": 31.6295, "lon": -7.9811},
    "Agadir":      {"lat": 30.4278, "lon": -9.5981},
    "Fes":         {"lat": 34.0181, "lon": -5.0078},
    "Oujda":       {"lat": 34.6814, "lon": -1.9086},
    "Laayoune":    {"lat": 27.1253, "lon": -13.1625}
}

ROUTES = [
    ("Route-Nord", ["Tanger", "Rabat", "Casablanca"]),
    ("Route-Sud",  ["Casablanca", "Marrakech", "Agadir"]),
    ("Route-Est",  ["Rabat", "Fes", "Oujda"]),
    ("Route-Sahara", ["Agadir", "Laayoune"])
]

producer = KafkaProducer(
    bootstrap_servers=bootstrap,
    value_serializer=lambda v: json.dumps(v).encode("utf-8"),
    retries=5
)

print(f"[Producer] Connecté à {bootstrap}. Démarrage de la simulation Maroc...")

# ==========================================
# ÉTAT DES CAMIONS (Simulation de flotte)
# ==========================================
class TruckSim:
    def __init__(self, truck_id):
        self.id = f"pkg_{truck_id:03d}"
        self.route_name, self.stops = random.choice(ROUTES)
        self.current_leg = 0 # Index de la ville de départ actuelle
        self.progress = 0.0  # 0.0 à 1.0 (0% à 100% du trajet entre 2 villes)
        self.speed_factor = random.uniform(0.01, 0.03) # Vitesse de simulation

        # Position initiale
        start_city = self.stops[0]
        self.lat = CITIES[start_city]["lat"]
        self.lon = CITIES[start_city]["lon"]

    def move(self):
        # Déterminer le segment actuel (Ville A -> Ville B)
        city_a_name = self.stops[self.current_leg]
        city_b_name = self.stops[self.current_leg + 1]
        city_a = CITIES[city_a_name]
        city_b = CITIES[city_b_name]

        # Avancer
        self.progress += self.speed_factor

        # Interpolation linéaire (mouvement simple)
        self.lat = city_a["lat"] + (city_b["lat"] - city_a["lat"]) * self.progress
        self.lon = city_a["lon"] + (city_b["lon"] - city_a["lon"]) * self.progress

        # Gestion arrivée
        if self.progress >= 1.0:
            self.progress = 0.0
            self.current_leg += 1
            # Si fin de route, faire demi-tour ou recommencer (ici on recommence)
            if self.current_leg >= len(self.stops) - 1:
                self.current_leg = 0
                self.lat = CITIES[self.stops[0]]["lat"]
                self.lon = CITIES[self.stops[0]]["lon"]

        # Simulation capteurs
        temp = 25.0 + (35 - self.lat) * 1.5 + random.uniform(-2, 2) # Plus chaud au sud
        speed = 80.0 + random.uniform(-5, 5) # Vitesse autoroute
        hum = 55.0 + random.uniform(-5, 5)

        # Injection d'anomalie (Rare)
        if random.random() < 0.01:
            speed = 0 # Panne
            temp += 10 # Surchauffe

        return {
            "package_id": self.id,
            "timestamp": time.strftime('%Y-%m-%d %H:%M:%S'),
            "gps_latitude": self.lat,
            "gps_longitude": self.lon,
            "temperature": round(temp, 1),
            "humidity": round(hum, 1),
            "speed": round(speed, 1)
        }

# Créer une flotte de 3 camions pour le streaming
fleet = [TruckSim(1), TruckSim(2), TruckSim(3)]

try:
    while True:
        for truck in fleet:
            event = truck.move()
            producer.send("parcel_data", value=event)
            print(f"[Producer] {event['package_id']} ({truck.route_name}): Lat={event['gps_latitude']:.4f}, Lon={event['gps_longitude']:.4f}, Speed={event['speed']}")

        producer.flush()
        time.sleep(2) # Pause entre les envois

except KeyboardInterrupt:
    print("\n[Producer] Arrêt.")
=======

producer = KafkaProducer(
    bootstrap_servers='localhost:9092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

print("Live producer started. Sending to 3 topics (gps, temperature, battery)...")
print("Press Ctrl+C to stop.")

# Starting data
lat = 34.0208
lon = -6.8416
temp = 25.0
batt = 95.0  # Changed to percentage
speed = 60.0
humidity = 55.0
package_id = "pkg_001"

try:
    while True:
        # Simulate changes
        lat += random.uniform(-0.0005, 0.0005)
        lon += random.uniform(-0.0005, 0.0005)
        temp += random.uniform(-0.5, 0.5)
        batt -= 0.01  # Slow battery drain
        if batt < 0: batt = 0
        speed += random.uniform(-3, 3)
        if speed < 0: speed = 0

        current_time = time.strftime('%Y-%m-%d %H:%M:%S')

        # Create 3 separate messages with CORRECT field names matching Spark schema
        gps_data = {
            "package_id": package_id,  # Changed from "Route ID"
            "latitude": lat,           # Changed from "GPS Latitude"
            "longitude": lon,          # Changed from "GPS Longitude"
            "Speed": speed,
            "timestamp": current_time
        }

        temp_data = {
            "package_id": package_id,  # Changed from "Route ID"
            "temperature": temp,
            "Humidity": humidity,
            "timestamp": current_time
        }

        batt_data = {
            "package_id": package_id,  # Changed from "Route ID"
            "battery_level": batt,
            "battery_status": "healthy" if batt > 20 else "low",
            "timestamp": current_time
        }

        # Send to the 3 topics
        producer.send('gps', value=gps_data)
        producer.send('temperature', value=temp_data)
        producer.send('battery', value=batt_data)
        producer.flush()

        print(f"Sent data for {package_id} (lat: {lat:.4f}, temp: {temp:.1f}°C, battery: {batt:.1f}%)")

        time.sleep(5)

except KeyboardInterrupt:
    print("\nStopping producer.")
>>>>>>> 65b414ae64dcf8697ad125c249e20560e4fdea91
finally:
    producer.close()