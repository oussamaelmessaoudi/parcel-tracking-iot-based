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
  role: string;
}
// addd BackendSensorData interface
export interface BackendSensorData {
  dhtData: {
    temperature: number;
    humidity: number;
    timestamp: string;
  };
  gpsData: {
    longitude: number;
    latitude: number;
    satellites: number;
    timestamp: string;
  };
}
