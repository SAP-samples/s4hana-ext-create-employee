package com.sap.csc.employeecreationbe.model.soap;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SoapResponse {

	private String url;
	private final String externalId;
	private final HttpStatus status;
	private final String message;

}
