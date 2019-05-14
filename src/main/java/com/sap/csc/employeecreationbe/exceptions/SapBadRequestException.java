package com.sap.csc.employeecreationbe.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class SapBadRequestException extends SapException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5327522396596014165L;
	
	private String externalId;

    public static SapBadRequestException create(String exception) {
	    return new SapBadRequestException(exception);
	}

	public static SapBadRequestException create(String exception, String externalId) {
	    return new SapBadRequestException(exception, externalId);
	}

	private SapBadRequestException(String exception) {
        super(exception);
    }

    private SapBadRequestException(String exception, String externalId) {
        super(exception);
        this.externalId = externalId;
    }
    
}
