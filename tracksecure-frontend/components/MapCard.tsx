import React from 'react';
import { Coordinates } from '../types';

interface MapCardProps {
  coordinates: Coordinates;
}

const MapCard: React.FC<MapCardProps> = ({ coordinates }) => {
  // In a real application, you would integrate a map library like Leaflet, OpenLayers, or Google Maps.
  // For this demo, we'll display a static placeholder.
  const mapImageUrl = `https://via.placeholder.com/800x400.png?text=Map+View+of+${coordinates.lat},${coordinates.lon}`;

  return (
    <div className="bg-white rounded-xl shadow-lg p-4">
      <h3 className="text-lg font-semibold text-gray-800 mb-4">Carte de localisation en direct</h3>
      <div className="aspect-w-16 aspect-h-9 bg-gray-200 rounded-lg overflow-hidden flex items-center justify-center text-gray-500">
        {/* Placeholder for an interactive map */}
        <div className="w-full h-full bg-cover bg-center" style={{ backgroundImage: `url(${mapImageUrl})`, minHeight: '300px' }}>
             <div className="flex items-center justify-center h-full w-full bg-gray-800 bg-opacity-50">
                <p className="text-white text-lg font-bold">Intégration de carte interactive ici</p>
            </div>
        </div>
      </div>
       <div className="mt-4 text-sm text-gray-600">
        <p><strong>Latitude :</strong> {coordinates.lat.toFixed(6)}</p>
        <p><strong>Longitude :</strong> {coordinates.lon.toFixed(6)}</p>
      </div>
    </div>
  );
};

export default MapCard;
