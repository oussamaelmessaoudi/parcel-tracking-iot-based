import React, { useEffect, useRef } from 'react';
import { Coordinates } from '../types';

// Puisque Leaflet est chargé via un CDN, nous devons déclarer la variable globale 'L' 
// pour que TypeScript sache qu'elle existe.
declare var L: any;

interface MapCardProps {
  coordinatesHistory: Coordinates[];
}

const MapCard: React.FC<MapCardProps> = ({ coordinatesHistory }) => {
    const mapContainerRef = useRef<HTMLDivElement>(null);
    const mapRef = useRef<any>(null); // Pour conserver l'instance de la carte
    const markerRef = useRef<any>(null); // Pour conserver l'instance du marqueur
    const polylineRef = useRef<any>(null); // Pour conserver l'instance de la polyligne
    const startMarkerRef = useRef<any>(null); // Pour le marqueur de départ

    // Effet de nettoyage : s'exécute uniquement lorsque le composant est démonté
    useEffect(() => {
        return () => {
            if (mapRef.current) {
                mapRef.current.remove();
                mapRef.current = null;
            }
        };
    }, []);

    // Effet pour initialiser et mettre à jour la carte
    useEffect(() => {
        // S'assurer que le conteneur est prêt, que Leaflet est chargé et qu'il y a des données
        if (!mapContainerRef.current || typeof L === 'undefined' || coordinatesHistory.length === 0) {
            return;
        }

        const latLngs = coordinatesHistory.map(c => [c.lat, c.lon]);
        const latestLatLng = latLngs[latLngs.length - 1];
        const startLatLng = latLngs[0];

        // Initialiser la carte si elle n'existe pas encore
        if (!mapRef.current) {
            const map = L.map(mapContainerRef.current).setView(latestLatLng, 13);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            }).addTo(map);
            mapRef.current = map;
        }
        
        // Créer ou mettre à jour le marqueur de position actuelle
        if (!markerRef.current) {
            markerRef.current = L.marker(latestLatLng).addTo(mapRef.current);
        } else {
            markerRef.current.setLatLng(latestLatLng);
        }
        markerRef.current.bindPopup(`<b>Position Actuelle</b><br>Mise à jour à ${new Date().toLocaleTimeString()}`).openPopup();
        
        // Créer le marqueur de départ (une seule fois)
        if (!startMarkerRef.current) {
            startMarkerRef.current = L.circleMarker(startLatLng, {
                color: '#4338ca',
                fillColor: '#6366f1',
                fillOpacity: 1,
                radius: 8,
            }).addTo(mapRef.current).bindPopup('<b>Point de départ</b>');
        }

        // Créer ou mettre à jour la ligne du trajet
        if (latLngs.length > 1) {
            if (!polylineRef.current) {
                polylineRef.current = L.polyline(latLngs, { color: '#10b981', weight: 5, opacity: 0.8 }).addTo(mapRef.current);
            } else {
                polylineRef.current.setLatLngs(latLngs);
            }
            // Ajuster la vue pour afficher l'ensemble du trajet
            mapRef.current.fitBounds(polylineRef.current.getBounds(), { padding: [50, 50], maxZoom: 16 });
        } else {
            mapRef.current.flyTo(latestLatLng, 13);
        }

    }, [coordinatesHistory]); // Cet effet s'exécute à chaque changement de l'historique des coordonnées

    const latestCoords = coordinatesHistory.length > 0 ? coordinatesHistory[coordinatesHistory.length - 1] : null;

    return (
        <div className="bg-white rounded-xl shadow-lg p-4">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">Trajet du colis en direct</h3>
            {/* Le conteneur div pour la carte Leaflet */}
            <div ref={mapContainerRef} style={{ height: '400px', borderRadius: '8px', zIndex: 0 }} className="w-full"></div>
            <div className="mt-4 text-sm text-gray-600">
                <p><strong>Latitude :</strong> {latestCoords ? latestCoords.lat.toFixed(6) : "N/A"}</p>
                <p><strong>Longitude :</strong> {latestCoords ? latestCoords.lon.toFixed(6) : "N/A"}</p>
            </div>
        </div>
    );
};

export default MapCard;