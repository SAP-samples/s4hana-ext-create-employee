package com.sap.csc.employeecreationbe.model.employee.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Salutation {
	
    MR("1", "Mr", "MR"),
    
    MRS("2", "Mrs", "MRS");

    private final String code;
    private final String description;
    private final String key;
    
}
