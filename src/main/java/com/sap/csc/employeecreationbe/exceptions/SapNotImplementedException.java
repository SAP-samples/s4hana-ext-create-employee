package com.sap.csc.employeecreationbe.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
public class SapNotImplementedException extends SapException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9040580518203493017L;

	public static SapNotImplementedException create(String exception) {
	    return new SapNotImplementedException(exception);
	}

	private SapNotImplementedException(String exception) {
        super(exception);
    }
    
}
