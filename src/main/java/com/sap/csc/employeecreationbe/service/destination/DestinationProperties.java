package com.sap.csc.employeecreationbe.service.destination;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.Data;

@Profile({ "dev", "test" })
@ConfigurationProperties(prefix = "destination-list")
@Configuration
@Data
public class DestinationProperties {

	@Data
	public static class Destination {
		
		String name;
		String user;
		String password;
		String url;
		
	}
	
	List<Destination> destinations;

}
