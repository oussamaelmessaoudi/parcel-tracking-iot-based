import axios from 'axios';

const API_BASE_URL = 'http://localhost:1440';

export interface SensorPayload {
  deviceId: string;
  temperature: number;
  humidity: number;
  location: {
    latitude: number;
    longitude: number;
  };
  timestamp: string;
}

export const SensorService = {
  getLatestData: async (): Promise<SensorPayload> => {
    try {
      const response = await axios.get(`${API_BASE_URL}/sensor/data`);
      return response.data;
    } catch (error) {
      console.error('Error fetching sensor data:', error);
      throw error;
    }
  }
};