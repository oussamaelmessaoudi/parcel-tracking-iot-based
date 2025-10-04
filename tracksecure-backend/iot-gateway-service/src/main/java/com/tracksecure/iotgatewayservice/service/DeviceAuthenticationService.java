package com.tracksecure.iotgatewayservice.service;

import com.tracksecure.common.exception.DeviceNotAvailableException;
import com.tracksecure.iotgatewayservice.model.DeviceCredential;
import com.tracksecure.iotgatewayservice.repository.DeviceCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceAuthenticationService {
    private final DeviceCredentialRepository deviceCredentialRepository;

    public DeviceCredential authenticate(String deviceId){
        //For authentication we'll check either the device exists in the DB using it's ID otherwise an exception will be risen
        return deviceCredentialRepository.findById(deviceId)
                .filter(DeviceCredential::isActive)
                .orElseThrow(()-> new DeviceNotAvailableException("Device not active or not found :"+deviceId));
    }
}
