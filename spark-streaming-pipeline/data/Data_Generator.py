import pandas as pd
import numpy as np
import random
from datetime import datetime, timedelta
import math

# ==========================================
# 1. CONFIGURATION GÉOGRAPHIQUE (VILLES)
# ==========================================
# Coordonnées réelles des hubs logistiques au Maroc
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

# Définition des itinéraires courants (Route ID)
ROUTES = [
    ("Route-Nord", ["Tanger", "Rabat", "Casablanca"]),
    ("Route-Sud",  ["Casablanca", "Marrakech", "Agadir"]),
    ("Route-Est",  ["Rabat", "Fes", "Oujda"]),
    ("Route-Sahara", ["Agadir", "Laayoune"])
]

# ==========================================
# 2. PARAMÈTRES DE GÉNÉRATION
# ==========================================
NUM_TRUCKS = 50       # Nombre de camions simulés
DAYS_HISTORY = 90     # Historique de données (jours)
SAMPLES_PER_HOUR = 6  # Un ping toutes les 10 minutes

print(f"Génération du dataset pour tout le Maroc ({DAYS_HISTORY} jours)...")

# ==========================================
# 3. FONCTIONS UTILITAIRES
# ==========================================

def interpolate_points(start_city, end_city, num_points):
    """Génère des points GPS intermédiaires entre deux villes."""
    lats = np.linspace(CITIES[start_city]["lat"], CITIES[end_city]["lat"], num_points)
    lons = np.linspace(CITIES[start_city]["lon"], CITIES[end_city]["lon"], num_points)
    return lats, lons

def generate_weather(lat, month):
    """Simule la température selon la latitude et la saison."""
    base_temp = 25
    # Plus au sud (latitude plus basse) = plus chaud
    lat_factor = (35 - lat) * 1.5

    # Variation saisonnière (Hiver=Janvier, Été=Juillet)
    season_factor = -5 if month in [12, 1, 2] else (5 if month in [6, 7, 8] else 0)

    temp = base_temp + lat_factor + season_factor + np.random.normal(0, 3)
    return round(temp, 1)

# ==========================================
# 4. BOUCLE DE GÉNÉRATION
# ==========================================
data = []
start_date = datetime.now() - timedelta(days=DAYS_HISTORY)

for truck_id in range(1, NUM_TRUCKS + 1):
    # Assigner une route aléatoire à ce camion
    route_name, stops = random.choice(ROUTES)
    current_time = start_date

    # Simuler des allers-retours sur la route
    while current_time < datetime.now():
        # Choisir un segment (ex: Tanger -> Rabat)
        start_idx = random.randint(0, len(stops) - 2)
        city_a = stops[start_idx]
        city_b = stops[start_idx + 1]

        # Durée du trajet (variable selon la distance)
        dist_approx = math.sqrt((CITIES[city_a]["lat"]-CITIES[city_b]["lat"])**2 +
                                (CITIES[city_a]["lon"]-CITIES[city_b]["lon"])**2) * 100 # ~km facteur
        hours_needed = max(1, int(dist_approx / 60)) # Vitesse moyenne
        num_pings = hours_needed * SAMPLES_PER_HOUR

        lats, lons = interpolate_points(city_a, city_b, num_pings)

        for i in range(len(lats)):
            current_time += timedelta(minutes=10)
            if current_time > datetime.now(): break

            # Données Normales
            lat = lats[i] + np.random.normal(0, 0.002) # Petite variation GPS
            lon = lons[i] + np.random.normal(0, 0.002)
            speed = np.random.normal(70, 10) # Autoroute
            temp = generate_weather(lat, current_time.month)
            humidity = np.random.normal(60, 15)
            delay = 0
            risk = "Low"

            # --- INJECTION D'ANOMALIES (5% de chance) ---
            if random.random() < 0.05:
                anomaly_type = random.choice(["panne", "meteo", "traffic"])

                if anomaly_type == "panne":
                    speed = 0
                    delay = 1
                    risk = "High"
                elif anomaly_type == "meteo":
                    # Canicule à Marrakech ou Laayoune
                    if lat < 32:
                        temp += 15  # Température extrême > 45°C
                    humidity = 10
                    risk = "Medium"
                elif anomaly_type == "traffic":
                    speed = np.random.normal(10, 5) # Bouchon
                    delay = 1

            # Ajouter l'enregistrement
            data.append([
                f"TXN-{int(current_time.timestamp())}-{truck_id}",
                "Transit",
                f"Truck-{truck_id}",
                current_time.strftime("%Y-%m-%d %H:%M:%S"),
                f"RFID-{truck_id}",
                lat, lon, temp, humidity,
                route_name, speed,
                "On Time" if delay == 0 else "Delayed",
                risk, str(delay), "0", "None"
            ])

# ==========================================
# 5. EXPORT
# ==========================================
columns = [
    "Transaction ID", "Stage", "Entity", "Timestamp", "RFID ID",
    "GPS Latitude", "GPS Longitude", "Temperature", "Humidity",
    "Route ID", "Speed", "Delivery Status", "Risk Factor",
    "Delay Time", "Cost", "Disruption Type"
]

print("Création du DataFrame...")
df = pd.DataFrame(data, columns=columns)

output_path = "morocco_full_dataset.csv"
df.to_csv(output_path, index=False)

print(f"✅ TERMINÉ ! Dataset sauvegardé : {output_path}")
print(f"   Nombre de lignes : {len(df)}")
print(f"   Villes couvertes : {', '.join(CITIES.keys())}")