package com.sap.csc.employeecreationbe.model.employee.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@RequiredArgsConstructor
public enum WorkAgreementType {
	
    WORKFORCE_PERSON("1", "Workforce person", "WORKFORCE_PERSON"),
    
    FREELANCER("2", "Freelancer", "FREELANCER"),
    
    SERVICE_PERFORMER("3", "Service Performer", "SERVICE_PERFORMER");

    @JsonIgnore
    private final String code;
    
    private final String description;
    private final String key;
    
}
