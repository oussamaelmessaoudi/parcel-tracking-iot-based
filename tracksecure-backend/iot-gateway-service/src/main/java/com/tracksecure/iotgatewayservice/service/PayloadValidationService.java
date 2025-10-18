package com.tracksecure.iotgatewayservice.service;

import com.tracksecure.common.exception.InvalidPayloadException;
import com.tracksecure.iotgatewayservice.model.TelemetryPayload;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
//This automatically generates constructors for all final fields
public class PayloadValidationService {
    private final Validator validator;
    public void validate(TelemetryPayload payload){
        //Here we will ask the validator to find all the violations on the payload object and store them on a set
        Set<ConstraintViolation<TelemetryPayload>> violations = validator.validate(payload);
        if(!violations.isEmpty()){
            //if a violation exists it grabs the first one and then throw an exception
            String message = violations.iterator().next().getMessage();
            throw new InvalidPayloadException("Telemetry validation failed: " + message);
        }
    }
}



