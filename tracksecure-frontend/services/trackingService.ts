import { TrackingData, PackageData, BackendSensorData } from '../types';

// Simuler une table de colis en mémoire avec l'assignation utilisateur
const packages: PackageData[] = [
    { id: 'PKG-12345', username: 'user', departureLocation: 'Entrepôt A, Paris', pickupLocation: 'Client X, Lyon' },
    { id: 'PKG-ABCDE', username: 'user', departureLocation: 'Entrepôt B, Marseille', pickupLocation: 'Client Y, Lille' },
];

const API_URL = 'http://localhost:8081/api/sensor/latest';

/**
 * Récupère les dernières données de suivi depuis le backend Spring Boot.
 * @param packageId L'identifiant du colis
 * @param token Le jeton d'accès JWT de Keycloak
 */
export const fetchTrackingData = async (packageId: string, token?: string): Promise<TrackingData> => {
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };

  // Si le token est présent, l'ajouter à l'en-tête Authorization
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(API_URL, {
    method: 'GET',
    headers: headers
  }); 

  if (!response.ok) {
    if (response.status === 401) {
        throw new Error(`Non authentifié (statut: 401). Veuillez vous reconnecter.`);
    }
    if (response.status === 403) {
        throw new Error(`Accès non autorisé (statut: 403). Le rôle de l'utilisateur n'a peut-être pas la permission.`);
    }
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

/**
 * Simule la mise à jour d'un colis existant.
 */
export const updatePackage = (originalPackageId: string, updatedData: { username: string; departureLocation: string; pickupLocation: string }): Promise<PackageData> => {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            const packageIndex = packages.findIndex(p => p.id === originalPackageId);
            if (packageIndex === -1) {
                return reject(new Error("Le colis n'a pas été trouvé."));
            }

            if (!updatedData.username) {
                return reject(new Error('Un utilisateur doit être sélectionné.'));
            }
            if (!updatedData.departureLocation.trim() || !updatedData.pickupLocation.trim()) {
                return reject(new Error('Les lieux de départ et de prise en charge sont requis.'));
            }

            const currentPackage = packages[packageIndex];
            const updatedPackage = {
                ...currentPackage,
                username: updatedData.username,
                departureLocation: updatedData.departureLocation,
                pickupLocation: updatedData.pickupLocation,
            };
            packages[packageIndex] = updatedPackage;

            resolve(updatedPackage);
        }, 300);
    });
};

/**
 * Simule la suppression d'un colis.
 */
export const deletePackage = (packageId: string): Promise<void> => {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            const packageIndex = packages.findIndex(p => p.id === packageId);
            if (packageIndex === -1) {
                return reject(new Error("Le colis n'a pas été trouvé."));
            }
            packages.splice(packageIndex, 1);
            resolve();
        }, 300);
    });
};