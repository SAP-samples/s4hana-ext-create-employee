package com.sap.csc.employeecreationbe.model.employee.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@RequiredArgsConstructor
public enum NamePrefix {
	
    DE("200", "de", "DE"),
    
    DE_LA("177", "de la", "DE_LA"),
    
    DOS("182", "dos", "DOS"),
    
    DU("47", "du", "DU"),
    
    EL("58", "el", "EL"),
    
    VAN("60", "van", "VAN"),
    
    VAN_DER("84", "van der", "VAN_DER"),
    
    VON("86", "von", "VON"),
    
    VON_DER("89", "von der", "VON_DER");

    private final String code;
    private final String description;
    private final String key;
    
}
