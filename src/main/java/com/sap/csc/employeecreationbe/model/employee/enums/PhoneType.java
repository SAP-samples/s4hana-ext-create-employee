package com.sap.csc.employeecreationbe.model.employee.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@RequiredArgsConstructor
public enum PhoneType {
	
    ECPC("ECPC", "Cell Phone"),
    
    ECPB("ECPB", "Landline");

    private final String code;
    private final String description;
    
}
