import { TrackingData } from '../types';

// Appel réel à l'API Spring Boot
export const fetchTrackingData = async (): Promise<TrackingData> => {
  try {
    const response = await fetch('http://localhost:1440/sensor/data'); // Remplace par ton endpoint réel
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data: TrackingData = await response.json();
    return data;
  } catch (error) {
    console.error('Erreur lors de la récupération des données de suivi :', error);
    // Retourne éventuellement des données par défaut pour ne pas casser l'app
    return {
      packageId: 'PKG-ERROR',
      temperature: 0,
      humidity: 0,
      coordinates: { lat: 0, lon: 0 },
      lastUpdated: new Date().toISOString(),
    };
  }
};
