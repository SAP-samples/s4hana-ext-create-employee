package com.sap.csc.employeecreationbe.service.employee;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.s4hana.connectivity.soap.SoapException;
import com.sap.cloud.sdk.s4hana.connectivity.soap.SoapQuery;
import com.sap.csc.employeecreationbe.config.Constants;
import com.sap.csc.employeecreationbe.exceptions.SapException;
import com.sap.csc.employeecreationbe.exceptions.SapNotFoundException;
import com.sap.csc.employeecreationbe.service.SoapClient;
import com.sap.csc.employeecreationbe.workforce.WorkforcePersonMasterDataReplicationServiceStub;
import com.sap.xi.pasein.WorkforcePersonMasterData;
import com.sap.xi.pasein.WorkforcePersonMasterDataReplResponse;
import com.sap.xi.pasein.WorkforcePersonMasterDataReplicationRequest;
import com.sap.xi.pasein.WorkforcePersonMasterDataReplicationRequest_sync;
import com.sap.xi.pasein.WorkforcePersonMasterDataReplicationResponse_sync;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmployeeClient implements SoapClient<WorkforcePersonMasterData, WorkforcePersonMasterDataReplResponse> {

	@Override
	public WorkforcePersonMasterDataReplResponse send(WorkforcePersonMasterData employee) {
		final WorkforcePersonMasterDataReplicationRequest request = new WorkforcePersonMasterDataReplicationRequest();
		request.addWorkforcePersonMasterData(employee);

		final WorkforcePersonMasterDataReplicationRequest_sync requestSync = new WorkforcePersonMasterDataReplicationRequest_sync();
		requestSync.setWorkforcePersonMasterDataReplicationRequest_sync(request);

		try {
			final SoapQuery<WorkforcePersonMasterDataReplicationServiceStub> soapQuery = new SoapQuery<>(
					WorkforcePersonMasterDataReplicationServiceStub.class, erp());
			
			final WorkforcePersonMasterDataReplicationResponse_sync response = soapQuery
					.execute(s -> s.workforcePersonMasterDataReplicationRequestResponse_In(requestSync));

			return Stream
					.of(response.getWorkforcePersonMasterDataReplicationResponse_sync()
							.getWorkforcePersonMasterDataReplResponse())
					.findFirst()
					.orElseThrow(() -> SapNotFoundException.create("Error while getting response"));
		} catch (SoapException e) {
			log.info("Exception occured when trying to connect to soap service", e);
			throw SapException.create("Failed to connect to soap service: " + e.getMessage());
		}
	}

	@Override
	public List<WorkforcePersonMasterDataReplResponse> sendAll(List<WorkforcePersonMasterData> employees) {
		final WorkforcePersonMasterDataReplicationRequest request = new WorkforcePersonMasterDataReplicationRequest();
		
		employees.forEach(request::addWorkforcePersonMasterData);

		final WorkforcePersonMasterDataReplicationRequest_sync requestSync = new WorkforcePersonMasterDataReplicationRequest_sync();
		requestSync.setWorkforcePersonMasterDataReplicationRequest_sync(request);

		try {
			final SoapQuery<WorkforcePersonMasterDataReplicationServiceStub> soapQuery;
			soapQuery = new SoapQuery<>(WorkforcePersonMasterDataReplicationServiceStub.class, erp());

			final WorkforcePersonMasterDataReplicationResponse_sync response = soapQuery
					.execute(s -> s.workforcePersonMasterDataReplicationRequestResponse_In(requestSync));
			
			return Arrays.asList(response.getWorkforcePersonMasterDataReplicationResponse_sync()
					.getWorkforcePersonMasterDataReplResponse());
		} catch (SoapException e) {
			log.info("Exception occured when trying to connect to soap service", e);
			throw SapException.create("Failed to connect to soap service: " + e.getMessage());
		}
	}

	private static ErpConfigContext erp() {
		return new ErpConfigContext(Constants.REPLICATE_WORKFORCE);
	}

}
