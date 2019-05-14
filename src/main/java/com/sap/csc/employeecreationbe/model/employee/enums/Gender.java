package com.sap.csc.employeecreationbe.model.employee.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Gender {
	
    MALE("1", "Male", "MALE"),
    
    FEMALE("2", "Female", "FEMALE");

    @JsonIgnore
    private final String code;
    
    private final String description;
    private final String key;
    
}
