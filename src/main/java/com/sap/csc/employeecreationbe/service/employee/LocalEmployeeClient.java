package com.sap.csc.employeecreationbe.service.employee;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sap.csc.employeecreationbe.service.SoapClient;
import com.sap.xi.pasein.WorkforcePersonMasterData;
import com.sap.xi.pasein.WorkforcePersonMasterDataReplResponse;

@Profile("test")
@Component
public class LocalEmployeeClient implements SoapClient<WorkforcePersonMasterData, WorkforcePersonMasterDataReplResponse> {
	
    @Override
    public WorkforcePersonMasterDataReplResponse send(WorkforcePersonMasterData value) {
        return null;
    }

    @Override
    public List<WorkforcePersonMasterDataReplResponse> sendAll(List<WorkforcePersonMasterData> values) {
        return null;
    }
    
}
