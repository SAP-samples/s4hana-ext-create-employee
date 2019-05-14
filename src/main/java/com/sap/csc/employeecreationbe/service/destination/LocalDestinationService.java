package com.sap.csc.employeecreationbe.service.destination;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.csc.employeecreationbe.exceptions.SapException;

@Profile({"dev", "test"})
@Component
public class LocalDestinationService implements DestinationService {

    private final DestinationProperties destinationProperties;

    @Autowired
    public LocalDestinationService(DestinationProperties destinationProperties) {
        this.destinationProperties = destinationProperties;
    }

    @Override
    public DestinationProperties.Destination find(String name) {
        return destinationProperties.getDestinations()
                .stream()
                .filter(d -> d.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> SapException.create("Destination: " + name + " not found!"));
    }
    
}
