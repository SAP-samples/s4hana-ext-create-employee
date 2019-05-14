package com.sap.csc.employeecreationbe.service.destination;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.sap.csc.employeecreationbe.service.destination.DestinationProperties.Destination;

@Profile({"dev", "test"})
@Service
public interface DestinationService {

    Destination find(String name);
    
}
