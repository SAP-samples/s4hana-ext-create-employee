package com.sap.csc.employeecreationbe.service.businessuser;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.axis2.databinding.types.Token;
import org.springframework.stereotype.Component;

import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.s4hana.connectivity.soap.SoapException;
import com.sap.cloud.sdk.s4hana.connectivity.soap.SoapQuery;
import com.sap.csc.employeecreationbe.business.query.QueryBusinessUserServiceStub;
import com.sap.csc.employeecreationbe.config.Constants;
import com.sap.csc.employeecreationbe.exceptions.SapException;
import com.sap.csc.employeecreationbe.exceptions.SapNotFoundException;
import com.sap.csc.employeecreationbe.model.dto.QueryBusinessUserDTO;
import com.sap.csc.employeecreationbe.service.SoapClient;
import com.sap.xi.aba.BusinessUserSimpleByElementsQuery_sync;
import com.sap.xi.aba.BusinessUserSimplyByElementsQuery;
import com.sap.xi.aba.BusinessUserSimplyByElementsQueryMessage_sync;
import com.sap.xi.aba.BusinessUserSimplyByElementsResponse;
import com.sap.xi.aba.BusinessUserSimplyByElementsResponse_sync;
import com.sap.xi.aba.NOSC_Indicator;
import com.sap.xi.aba.NOSC_IntervalBoundaryTypeCode;
import com.sap.xi.aba.NOSC_UserName;
import com.sap.xi.aba.QueryProcessingConditions;
import com.sap.xi.aba.UserNameInterval;

import lombok.extern.slf4j.Slf4j;

/**
 * Used to retrieve existing business users in the SAP S/4HANA system.
 */
@Slf4j
@Component
public class QueryBusinessUserClient implements SoapClient<String, QueryBusinessUserDTO> {

	private static final String EQUAL = "1";

	/**
	 * @return a business user with the given {@code userName}
	 */
	@Override
	public QueryBusinessUserDTO send(String userName) {
		final List<String> userNames = Collections.singletonList(userName);
		log.info("Request: {}", userNames);

		try {
			//Set-up the service with the ERP configuration
			final SoapQuery<QueryBusinessUserServiceStub> soapQuery = new SoapQuery<>(
					QueryBusinessUserServiceStub.class, erp());

			//Send request to QueryBusinessUser API
			final BusinessUserSimplyByElementsResponse_sync execute = soapQuery
					.execute(service -> service.queryBusinessUserIn(createRequest(userNames)));

			//Response containing the queried user (only 1 value in this case because username is unique)
			final BusinessUserSimplyByElementsResponse[] businessUser = Optional
					.ofNullable(execute.getBusinessUserSimplyByElementsResponse_sync().getBusinessUser())
					.orElse(new BusinessUserSimplyByElementsResponse[] {});

			//Get user from response
			final BusinessUserSimplyByElementsResponse businessUserSimplyByElementsResponse = Stream.of(businessUser)
					.findFirst()
					.orElseThrow(() -> SapNotFoundException.create("No business user found for user name: " + userName));
			
			//Convert user from response to DTO
			final QueryBusinessUserDTO response = QueryBusinessUserDTO.of(businessUserSimplyByElementsResponse);
			log.info("Response: {}", response);

			return response;
		} catch (SoapException e) {
			log.info("Exception occured when trying to connect to soap service", e);
			throw SapException.create("Failed to connect to soap service: " + e.getMessage());
		}
	}

	/**
	 * @return business users with the given {@code userNames}
	 */
	@Override
	public List<QueryBusinessUserDTO> sendAll(List<String> userNames) {
		log.info("Request: {}", userNames);

		try {
			//Set-up the service with the ERP configuration
			final SoapQuery<QueryBusinessUserServiceStub> soapQuery = new SoapQuery<>(
					QueryBusinessUserServiceStub.class, erp());

			//Send request to QueryBusinessUser API
			final BusinessUserSimplyByElementsResponse_sync execute = soapQuery
					.execute(service -> service.queryBusinessUserIn(createRequest(userNames)));

			//Response containing the queried users
			final BusinessUserSimplyByElementsResponse[] businessUsers = Optional
					.ofNullable(execute.getBusinessUserSimplyByElementsResponse_sync().getBusinessUser())
					.orElse(new BusinessUserSimplyByElementsResponse[] {});

			//Convert users from response to DTOs
			final List<QueryBusinessUserDTO> response = Stream.of(businessUsers).map(QueryBusinessUserDTO::of)
					.collect(Collectors.toList());
			log.info("Response: {}", response);

			return response;
		} catch (SoapException e) {
			log.info("Exception occured when trying to connect to soap service", e);
			throw SapException.create("Failed to connect to soap service: " + e.getMessage());
		}
	}

	private static BusinessUserSimpleByElementsQuery_sync createRequest(List<String> userNames) {
		//Create the request object that will be sent to the API
		final NOSC_IntervalBoundaryTypeCode code = new NOSC_IntervalBoundaryTypeCode();
		code.setNOSC_IntervalBoundaryTypeCode(new Token(EQUAL));

		//Filter for username
		List<UserNameInterval> userNameIntervals = userNames.stream().map(u -> {
			final NOSC_UserName username = new NOSC_UserName();
			username.setNOSC_UserName(u);

			final UserNameInterval userNameInterval = new UserNameInterval();
			userNameInterval.setIntervalBoundaryTypeCode(code);
			userNameInterval.setLowerBoundaryUserName(username);

			return userNameInterval;
		}).collect(Collectors.toList());

		final BusinessUserSimplyByElementsQuery businessUsers = new BusinessUserSimplyByElementsQuery();
		userNameIntervals.forEach(businessUsers::addUserNameInterval);

		final NOSC_Indicator indicator = new NOSC_Indicator();
		indicator.setNOSC_Indicator(false);

		final QueryProcessingConditions conditions = new QueryProcessingConditions();
		conditions.setQueryHitsUnlimitedIndicator(indicator);

		final BusinessUserSimplyByElementsQueryMessage_sync syncMessage = new BusinessUserSimplyByElementsQueryMessage_sync();
		syncMessage.setBusinessUser(businessUsers);
		syncMessage.setQueryProcessingConditions(conditions);

		final BusinessUserSimpleByElementsQuery_sync message = new BusinessUserSimpleByElementsQuery_sync();
		message.setBusinessUserSimpleByElementsQuery_sync(syncMessage);

		return message;
	}

	private static ErpConfigContext erp() {
		return new ErpConfigContext(Constants.REPLICATE_WORKFORCE);
	}

}
