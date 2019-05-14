package com.sap.csc.employeecreationbe.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
class ApiError {

    private HttpStatus status;
    private String message;
    private String debugMessage;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    
    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    ApiError(HttpStatus status, Throwable ex) {
        this();
        this.status = status;
        this.message = ex.getMessage();
        this.debugMessage = ex.getLocalizedMessage();
    }

    ApiError(HttpStatus status, String message, Throwable ex) {
        this();
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }

    ApiError(HttpStatus status, Map<String, Object> errorAttributes) {
        this();
        this.status = status;
        this.message = (String) errorAttributes.get("error");
        this.debugMessage = (String) errorAttributes.get("trace");
    }
    
}