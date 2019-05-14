package com.sap.csc.employeecreationbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableConfigurationProperties
@SpringBootApplication
@EnableScheduling
@ServletComponentScan("com.sap.cloud.sdk")
public class EmployeeCreationBeApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeCreationBeApplication.class, args);
    }
    
}
