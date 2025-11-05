import { TrackingData, PackageData, BackendSensorData } from '../types';

// Simuler une table de colis en mémoire avec l'assignation utilisateur
const packages: PackageData[] = [
    { id: 'PKG-12345', username: 'user', departureLocation: 'Entrepôt A, Paris', pickupLocation: 'Client X, Lyon' },
    { id: 'PKG-ABCDE', username: 'user', departureLocation: 'Entrepôt B, Marseille', pickupLocation: 'Client Y, Lille' },
];

/**
 * Récupère les dernières données de suivi depuis le backend Spring Boot.
 */
export const fetchTrackingData = async (packageId: string): Promise<TrackingData> => {
  // const response = await fetch('http://localhost:8080/sensor-data');
  const response = await fetch('/sensor-data');
  if (!response.ok) {
    throw new Error(`Erreur HTTP ! statut: ${response.status}`);
  }
  const backendData: BackendSensorData = await response.json();

  const transformedData: TrackingData = {
    temperature: backendData.dhtData.temperature,
    humidity: backendData.dhtData.humidity,
    coordinates: {
      lat: backendData.gpsData.latitude,
      lon: backendData.gpsData.longitude,
    },
    timestamp: backendData.dhtData.timestamp,
    packageId: packageId,
  };
  
  return transformedData;
};


/**
 * Simule la création d'un nouveau colis et l'assigne à un utilisateur.
 */
export const createPackage = (packageId: string, username: string, departureLocation: string, pickupLocation: string): Promise<PackageData> => {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            if (packages.some(p => p.id === packageId)) {
                return reject(new Error('Ce numéro de colis existe déjà.'));
            }
            if (!packageId.trim()) {
                return reject(new Error('Le numéro de colis ne peut pas être vide.'));
            }
            if (!username) {
                return reject(new Error('Un utilisateur doit être sélectionné.'));
            }
            if (!departureLocation.trim() || !pickupLocation.trim()) {
                return reject(new Error('Les lieux de départ et de prise en charge sont requis.'));
            }
            const newPackage = { id: packageId, username, departureLocation, pickupLocation };
            packages.push(newPackage);
            resolve(newPackage);
        }, 300);
    });
};

/**
 * Récupère la liste de tous les colis (pour l'admin).
 */
export const getPackages = (): Promise<PackageData[]> => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve([...packages]);
        }, 200);
    });
};

/**
 * Récupère les colis pour un utilisateur spécifique.
 */
export const getPackagesForUser = (username: string): Promise<PackageData[]> => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve(packages.filter(p => p.username === username));
        }, 200);
    });
};
