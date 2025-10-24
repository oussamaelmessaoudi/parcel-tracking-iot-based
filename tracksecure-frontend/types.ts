
export interface Coordinates {
  lat: number;
  lon: number;
}

export interface TrackingData {
  temperature: number;
  humidity: number;
  coordinates: Coordinates;
  lastUpdated: string;
  packageId: string;
}
