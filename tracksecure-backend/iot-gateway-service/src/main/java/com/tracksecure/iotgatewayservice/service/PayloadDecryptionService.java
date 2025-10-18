package com.tracksecure.iotgatewayservice.service;

import com.tracksecure.common.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class PayloadDecryptionService {
    public String decrypt(String encryptedPayload,String secretKey) {
        //we call the helper method "decrypt" defined in the utils-common microservice
        return EncryptionUtil.decrypt(encryptedPayload, secretKey);
    }
}
