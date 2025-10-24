
import React from 'react';
import { Coordinates } from '../types';
import { MapPinIcon } from './Icons';

interface MapCardProps {
  coordinates: Coordinates;
}

const MapCard: React.FC<MapCardProps> = ({ coordinates }) => {
  // Construire l'URL d'intégration OpenStreetMap.
  // Le paramètre bbox définit la zone de visualisation, et marker place une épingle.
  const zoomLevel = 0.008; // Une valeur plus petite = plus de zoom
  const mapUrl = `https://www.openstreetmap.org/export/embed.html?bbox=${coordinates.lon - zoomLevel},${coordinates.lat - zoomLevel},${coordinates.lon + zoomLevel},${coordinates.lat + zoomLevel}&layer=mapnik&marker=${coordinates.lat},${coordinates.lon}`;

  return (
    <div className="bg-slate-800 p-6 rounded-2xl shadow-lg md:col-span-2 flex flex-col justify-between transition-shadow hover:shadow-cyan-500/20 duration-300">
      <div>
        <div className="flex items-center mb-4">
          <MapPinIcon className="h-6 w-6 text-cyan-400 mr-3" />
          <h3 className="text-lg font-semibold text-white uppercase tracking-wider">Localisation Actuelle</h3>
        </div>
        <div className="aspect-video bg-slate-700 rounded-lg mb-4 overflow-hidden">
          <iframe
            title="Carte de localisation du colis"
            src={mapUrl}
            width="100%"
            height="100%"
            style={{ border: 0 }}
            allowFullScreen={false}
            loading="lazy"
            referrerPolicy="no-referrer-when-downgrade"
          ></iframe>
        </div>
      </div>
      <div className="grid grid-cols-2 gap-4 text-center">
        <div>
          <p className="text-xs text-slate-400 uppercase">Latitude</p>
          <p className="text-lg font-mono text-cyan-300">{coordinates.lat.toFixed(6)}</p>
        </div>
        <div>
          <p className="text-xs text-slate-400 uppercase">Longitude</p>
          <p className="text-lg font-mono text-cyan-300">{coordinates.lon.toFixed(6)}</p>
        </div>
      </div>
    </div>
  );
};

export default MapCard;
