package com.tracksecure.mqttrestapp.repository;

import com.tracksecure.mqttrestapp.model.SensorData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SensorDataRepository extends MongoRepository<SensorData, String> {
    
    // Find all data from a specific device
    List<SensorData> findByDeviceId(String deviceId);
    
    // Find latest records from a device
    List<SensorData> findTop10ByDeviceIdOrderByReceivedAtDesc(String deviceId);
    
    // Find data with temperature above threshold
    List<SensorData> findByDhtDataTemperatureGreaterThan(double temperature);
}