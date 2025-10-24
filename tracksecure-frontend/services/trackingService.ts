// trackingService.ts

import { TrackingData } from '../types';

// Appel réel à l'API Spring Boot
export const fetchTrackingData = async (): Promise<TrackingData> => {
  // Supprimez le try...catch d'ici.
  // Laissez App.tsx gérer les erreurs.
  
  const response = await fetch('http://localhost:1440/sensor/data');
  if (!response.ok) {
    // Si l'API renvoie une erreur (404, 500, etc.)
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  const data: TrackingData = await response.json();
  
  // Note : data.timestamp est déjà un string, pas besoin de .toString()
  return {
    temperature: data.temperature,
    humidity: data.humidity,
    timestamp: data.timestamp,
  };
  
  // Le bloc 'catch' a été supprimé. 
  // Si le fetch échoue (CORS, réseau coupé), l'erreur "montera"
  // jusqu'au 'catch' de la fonction 'loadData' dans App.tsx.
};