import React, { useEffect, useState } from 'react';
import { SensorService, SensorPayload } from '../services/api';

const SensorData: React.FC = () => {
  const [sensorData, setSensorData] = useState<SensorPayload | null>(null);
  const [error, setError] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(true);

  const fetchSensorData = async () => {
    try {
      const data = await SensorService.getLatestData();
      setSensorData(data);
      setError('');
    } catch (err) {
      setError('Failed to fetch sensor data');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSensorData();
    // Fetch data every 5 seconds
    const interval = setInterval(fetchSensorData, 5000);
    return () => clearInterval(interval);
  }, []);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!sensorData) return <div>No sensor data available</div>;

  return (
    <div className="sensor-data">
      <h2>Sensor Data</h2>
      <div className="sensor-info">
        <p><strong>Device ID:</strong> {sensorData.deviceId}</p>
        <p><strong>Temperature:</strong> {sensorData.temperature}°C</p>
        <p><strong>Humidity:</strong> {sensorData.humidity}%</p>
        <p><strong>Location:</strong> {sensorData.location.latitude}, {sensorData.location.longitude}</p>
        <p><strong>Last Updated:</strong> {new Date(sensorData.timestamp).toLocaleString()}</p>
      </div>
      <button onClick={fetchSensorData}>Refresh Data</button>
    </div>
  );
};

export default SensorData;