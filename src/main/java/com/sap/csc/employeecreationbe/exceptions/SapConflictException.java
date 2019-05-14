package com.sap.csc.employeecreationbe.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(HttpStatus.CONFLICT)
@Getter
public class SapConflictException extends SapException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6623267200536326233L;
	
	private String externalId;
	
    public static SapConflictException create(String exception) {
	    return new SapConflictException(exception);
	}
    
    public static SapConflictException create(String exception,String externalId) {
        return new SapConflictException(exception,externalId);
    }

	private SapConflictException(String exception) {
        super(exception);
    }

    private SapConflictException(String exception, String externalId) {
        super(exception);
        this.externalId = externalId;
    }
    
}
