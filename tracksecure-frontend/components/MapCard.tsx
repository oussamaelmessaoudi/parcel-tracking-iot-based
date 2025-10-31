import React, { useEffect, useRef } from 'react';
import { Coordinates } from '../types';

// Puisque Leaflet est chargé via un CDN, nous devons déclarer la variable globale 'L' 
// pour que TypeScript sache qu'elle existe.
declare var L: any;

interface MapCardProps {
  coordinates: Coordinates;
}

const MapCard: React.FC<MapCardProps> = ({ coordinates }) => {
    const mapContainerRef = useRef<HTMLDivElement>(null);
    const mapRef = useRef<any>(null); // Pour conserver l'instance de la carte
    const markerRef = useRef<any>(null); // Pour conserver l'instance du marqueur

    // Effet pour initialiser la carte une seule fois
    useEffect(() => {
        // S'assurer que le conteneur est prêt et que Leaflet est chargé
        if (mapContainerRef.current && !mapRef.current && typeof L !== 'undefined') {
            // Initialiser la carte et la centrer sur les coordonnées du colis
            const map = L.map(mapContainerRef.current).setView([coordinates.lat, coordinates.lon], 13);
            
            // Ajouter une couche de tuiles OpenStreetMap
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            }).addTo(map);

            // Ajouter un marqueur à la position du colis
            const marker = L.marker([coordinates.lat, coordinates.lon]).addTo(map)
                .bindPopup('Position actuelle du colis.')
                .openPopup();
            
            // Sauvegarder les instances de la carte et du marqueur dans les refs
            mapRef.current = map;
            markerRef.current = marker;
        }

        // Fonction de nettoyage : supprime l'instance de la carte lorsque le composant est démonté
        return () => {
            if (mapRef.current) {
                mapRef.current.remove();
                mapRef.current = null;
            }
        };
    }, []); // Le tableau de dépendances vide assure que cet effet ne s'exécute qu'une seule fois

    // Effet pour mettre à jour la carte lorsque les coordonnées changent
    useEffect(() => {
        // Vérifier si la carte et le marqueur ont bien été initialisés
        if (mapRef.current && markerRef.current) {
            const newLatLng = [coordinates.lat, coordinates.lon];
            
            // Déplacer la vue de la carte de manière fluide vers les nouvelles coordonnées
            mapRef.current.flyTo(newLatLng, 13);
            
            // Mettre à jour la position du marqueur
            markerRef.current.setLatLng(newLatLng);
        }
    }, [coordinates]); // Cet effet s'exécute à chaque changement des coordonnées

    return (
        <div className="bg-white rounded-xl shadow-lg p-4">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">Carte de localisation en direct</h3>
            {/* Le conteneur div pour la carte Leaflet */}
            <div ref={mapContainerRef} style={{ height: '400px', borderRadius: '8px', zIndex: 0 }} className="w-full"></div>
            <div className="mt-4 text-sm text-gray-600">
                <p><strong>Latitude :</strong> {coordinates.lat.toFixed(6)}</p>
                <p><strong>Longitude :</strong> {coordinates.lon.toFixed(6)}</p>
            </div>
        </div>
    );
};

export default MapCard;