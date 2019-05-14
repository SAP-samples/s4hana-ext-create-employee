package com.sap.csc.employeecreationbe.model.employee.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AcademicTitle {
	
    DR("0001", "Dr.", "DR"),
    
    PROF("0002", "Prof.", "PROF"),
    
    PROF_DR("0003", "Prof.Dr,", "PROF_DR"),
    
    BA("0004", "B.A.", "BA"),
    
    MBA("0005", "M.B.A.", "MBA"),
    
    PHD("0006", "PH.D.", "PHD");

    private final String code;
    private final String description;
    private final String key;
    
}
