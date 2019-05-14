package com.sap.csc.employeecreationbe.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SapNotFoundException extends SapException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8143792626878968574L;

	public static SapNotFoundException create(String exception) {
	    return new SapNotFoundException(exception);
	}

	private SapNotFoundException(String exception) {
        super(exception);
    }
    
}
