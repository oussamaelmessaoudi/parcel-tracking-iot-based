export interface Coordinates {
  lat: number;
  lon: number;
}

export interface TrackingData {
  temperature: number;
  humidity: number;
  coordinates: Coordinates;
  timestamp: string;
  packageId: string;
}

export interface PackageData {
    id: string;
    username: string; // L'utilisateur à qui le colis est assigné
    departureLocation: string;
    pickupLocation: string;
}

export interface User {
  username: string;
  role: 'utilisateur' | 'administrateur';
}