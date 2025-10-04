package com.tracksecure.iotgatewayservice.repository;

import com.tracksecure.iotgatewayservice.model.DeviceCredential;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceCredentialRepository extends CrudRepository<DeviceCredential,String> {
    boolean existsBySerialNumber(String serialNumber);
    DeviceCredential findBySerialNumber(String serialNumber);
}
