package com.sap.csc.employeecreationbe.model.employee.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NameSupplement {
	
    EARL("1", "Earl", "EARL"),
    
    FREIFRAU("2", "Freifrau", "FREIFRAU"),
    
    FREIHERR("3", "Freiherr", "FREIHERR"),
    
    FRUST("4", "Frust", "FRUST"),
    
    FRUSTIN("5", "Frustin", "FRUSTIN"),
    
    GRAF("6", "Graf", "GRAF"),
    
    GRAFIN("7", "Grafin", "GRAFIN"),
    
    SIR("8", "Sir", "SIR");

    private final String code;
    private final String description;
    private final String key;
    
}
