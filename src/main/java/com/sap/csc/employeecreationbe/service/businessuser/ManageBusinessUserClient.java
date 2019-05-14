package com.sap.csc.employeecreationbe.service.businessuser;

import static com.sap.csc.employeecreationbe.config.Constants.REPLICATE_WORKFORCE;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.s4hana.connectivity.soap.SoapException;
import com.sap.cloud.sdk.s4hana.connectivity.soap.SoapQuery;
import com.sap.csc.employeecreationbe.business.manage.ManageBusinessuserServiceStub;
import com.sap.csc.employeecreationbe.exceptions.SapException;
import com.sap.csc.employeecreationbe.service.SoapClient;
import com.sap.xi.aba.BusinessUserBundleMaintainConfirmation_sync;
import com.sap.xi.aba.BusinessUserBundleMaintainRequest_sync;
import com.sap.xi.aba.BusinessUserMaintainConfirmationBundle;
import com.sap.xi.aba.BusinessUserMaintainRequestBundle;
import com.sap.xi.aba.BusinessUserMaintainRequestBundleMessage_sync;

import lombok.extern.slf4j.Slf4j;

/**
 * Used to update business user in SAP S/4HANA system via the Business User -
 * Create, Update, Delete SOAP service
 * 
 * @see <a href= "https://api.sap.com/api/MANAGEBUSINESSUSERIN/documentation">
 *      SAP API Business Hub</a> for details of the Business User - Create,
 *      Update, Delete SOAP service
 */
@Slf4j
@Component
public class ManageBusinessUserClient
		implements SoapClient<BusinessUserMaintainRequestBundle, BusinessUserMaintainConfirmationBundle> {

	/**
	 * @param value A single business user update
	 * @return SOAP response of the API
	 */
	@Override
	public BusinessUserMaintainConfirmationBundle send(BusinessUserMaintainRequestBundle value) {
		final BusinessUserMaintainRequestBundleMessage_sync message = new BusinessUserMaintainRequestBundleMessage_sync();

		// A single user in request
		message.addBusinessUser(value);

		final BusinessUserBundleMaintainRequest_sync request = new BusinessUserBundleMaintainRequest_sync();
		request.setBusinessUserBundleMaintainRequest_sync(message);

		try {
			// Set-up the service with the ERP configuration
			final SoapQuery<ManageBusinessuserServiceStub> service = new SoapQuery<>(
					ManageBusinessuserServiceStub.class, erp());

			// Send request to ManageBusinessUser API
			final BusinessUserBundleMaintainConfirmation_sync response = service
					.execute(s -> s.manageBusinessUserIn(request));

			// Confirmation message of the updated users
			final BusinessUserMaintainConfirmationBundle[] businessUser = response
					.getBusinessUserBundleMaintainConfirmation_sync()
					.getBusinessUser();

			return Stream.of(businessUser)
					.findFirst()
					.orElse(new BusinessUserMaintainConfirmationBundle());
		} catch (SoapException e) {
			log.info("Exception occured when trying to connect to soap service", e);
			throw SapException.create("Failed to connect to soap service: " + e.getMessage());
		}
	}

	/**
	 * @param values Multiple business users update
	 * @return SOAP response of the API
	 */
	@Override
	public List<BusinessUserMaintainConfirmationBundle> sendAll(List<BusinessUserMaintainRequestBundle> values) {
		final BusinessUserMaintainRequestBundleMessage_sync message = new BusinessUserMaintainRequestBundleMessage_sync();

		// Add all users to the request
		values.forEach(message::addBusinessUser);

		final BusinessUserBundleMaintainRequest_sync request = new BusinessUserBundleMaintainRequest_sync();
		request.setBusinessUserBundleMaintainRequest_sync(message);

		try {
			// Set-up the service with the ERP configuration
			final SoapQuery<ManageBusinessuserServiceStub> service = new SoapQuery<>(
					ManageBusinessuserServiceStub.class, erp());

			// Send request to ManageBusinessUser API
			final BusinessUserBundleMaintainConfirmation_sync response = service
					.execute(s -> s.manageBusinessUserIn(request));

			// Confirmation message of the updated users
			final BusinessUserMaintainConfirmationBundle[] businessUser = response
					.getBusinessUserBundleMaintainConfirmation_sync()
					.getBusinessUser();

			return Arrays.asList(businessUser);
		} catch (SoapException e) {
			log.info("Exception occured when trying to connect to soap service", e);
			throw SapException.create("Failed to connect to soap service: " + e.getMessage());
		}
	}

	/**
	 * @return the ERP context of the SAP S/4HANA System
	 */
	private static ErpConfigContext erp() {
		return new ErpConfigContext(REPLICATE_WORKFORCE);
	}

}
