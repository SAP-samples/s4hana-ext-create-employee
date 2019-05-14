package com.sap.csc.employeecreationbe.model.employee.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WorkAgreementStatus {
    
	INACTIVE("0", "Inactive"),
	
    ACTIVE("1", "Active");

    private final String code;
    private final String description;
    
}
