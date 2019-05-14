package com.sap.csc.employeecreationbe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class RestConfig {
	
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        return new MappingJackson2HttpMessageConverter(mapper);
    }
    
}
