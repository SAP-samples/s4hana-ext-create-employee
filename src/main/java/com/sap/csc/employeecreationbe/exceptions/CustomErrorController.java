package com.sap.csc.employeecreationbe.exceptions;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";
    
    private final ErrorAttributes errorAttributes;
    
    @Value("${custom-error-controller.debug:true}")
    private boolean debug;

    @Autowired
    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @RequestMapping(value = PATH)
    ResponseEntity<ApiError> error(WebRequest webRequest, HttpServletResponse response) {
        final ApiError error = new ApiError(HttpStatus.valueOf(response.getStatus()), getErrorAttributes(webRequest, debug));
        
		return ResponseEntity.status(response.getStatus()).body(error);
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    private Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        return errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
    }
    
}
