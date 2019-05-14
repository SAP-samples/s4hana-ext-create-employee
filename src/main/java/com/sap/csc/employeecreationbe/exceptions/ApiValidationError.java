package com.sap.csc.employeecreationbe.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
class ApiValidationError extends ApiError {

	private String externalId;

	ApiValidationError(HttpStatus value, Throwable ex) {
		super(value,ex);
	}

}
