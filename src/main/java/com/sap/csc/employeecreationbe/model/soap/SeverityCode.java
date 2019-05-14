package com.sap.csc.employeecreationbe.model.soap;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeverityCode {
	
    E("Error", HttpStatus.BAD_REQUEST),
    
    I("Info", HttpStatus.OK);

    private final String message;
    private final HttpStatus status;
}
