package com.sap.csc.employeecreationbe.exceptions;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String MALFORMED_JSON_REQUEST_ERROR = "Malformed JSON request";

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		final ServletWebRequest servletWebRequest = (ServletWebRequest) request;
		log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());

		return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, MALFORMED_JSON_REQUEST_ERROR, ex));
	}

	@ExceptionHandler(SapException.class)
	protected <V extends SapException> ResponseEntity<Object> handleSapExceptions(V ex) {
		ApiError apiError = new ApiError(getHttpStatus(ex), ex);
		
		apiError.setMessage(ex.getException());

		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(SapBadRequestException.class)
	protected <V extends SapBadRequestException> ResponseEntity<Object> handleSapExceptions(V ex) {
		ApiValidationError apiError = new ApiValidationError(getHttpStatus(ex), ex);

		apiError.setMessage(ex.getException());
		apiError.setExternalId(ex.getExternalId());

		return buildResponseEntity(apiError);
	}

	@ExceptionHandler(SapConflictException.class)
	protected <V extends SapConflictException> ResponseEntity<Object> handleSapExceptions(V ex) {
		ApiValidationError apiError = new ApiValidationError(getHttpStatus(ex), ex);

		apiError.setMessage(ex.getException());
		apiError.setExternalId(ex.getExternalId());

		return buildResponseEntity(apiError);
	}

	protected static <V extends SapException> HttpStatus getHttpStatus(V ex) {
		return ex.getClass().getAnnotation(ResponseStatus.class).value();
	}

	private static ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}

}