package com.sap.csc.employeecreationbe.config;

import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.testutil.MockDestination;
import com.sap.cloud.sdk.testutil.MockUtil;
import com.sap.csc.employeecreationbe.repository.UserRepository;
import com.sap.csc.employeecreationbe.service.SoapClient;
import com.sap.csc.employeecreationbe.service.destination.DestinationProperties;
import com.sap.csc.employeecreationbe.service.destination.DestinationService;
import com.sap.csc.employeecreationbe.service.employee.LocalEmployeeClient;
import com.sap.xi.pasein.WorkforcePersonMasterData;
import com.sap.xi.pasein.WorkforcePersonMasterDataReplResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static com.sap.csc.employeecreationbe.config.Constants.IDP_DESTINATION;
import static com.sap.csc.employeecreationbe.config.Constants.REPLICATE_WORKFORCE;
import static org.mockito.Mockito.mock;

import java.net.URI;

@Configuration
public class TestConfig {

	@Primary
	@Bean
	public SoapClient<WorkforcePersonMasterData, WorkforcePersonMasterDataReplResponse> employeeClient() {
		return new LocalEmployeeClient();
	}

	@Primary
	@Bean
	public UserRepository userRepository() {
		return mock(UserRepository.class);
	}

	@Profile({ "test" })
	@Bean
	public ErpConfigContext erpConfigContext(DestinationService destinationService) {
		final MockUtil mockUtil = new MockUtil();
		mockUtil.mockDefaults();

		final DestinationProperties.Destination replicateWorkforce = destinationService.find(REPLICATE_WORKFORCE);
		mockUtil.mockDestination(MockDestination.builder().name(replicateWorkforce.getName())
				.basicAuthentication(
						new BasicCredentials(replicateWorkforce.getUser(), replicateWorkforce.getPassword()))
				.uri(URI.create(replicateWorkforce.getUrl())).build());

		final DestinationProperties.Destination idp = destinationService.find(IDP_DESTINATION);
		mockUtil.mockDestination(MockDestination.builder().name(idp.getName())
				.basicAuthentication(new BasicCredentials(idp.getUser(), idp.getPassword()))
				.uri(URI.create(idp.getUrl())).build());

		return new ErpConfigContext(REPLICATE_WORKFORCE);
	}

}
