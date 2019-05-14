package com.sap.csc.employeecreationbe.exceptions;

import java.io.Serializable;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
@Getter
public class SapException extends RuntimeException implements Serializable {
	
    /**
     * 
     */
    private static final long serialVersionUID = -8750863341350036501L;
    
    private final String exception;

    public static SapException create(String exception) {
	    return new SapException(exception);
	}

	SapException(String exception) {
        this.exception = exception;
    }
    
}