package com.sap.csc.employeecreationbe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sap.cloud.sdk.s4hana.datamodel.odata.services.CompanyCodeService;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.CostCenterService;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultCompanyCodeService;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultCostCenterService;

@Configuration
public class S4Beans {

    @Bean
    public CompanyCodeService companyCodeService() {
        return new DefaultCompanyCodeService();
    }

    @Bean
    public CostCenterService costCenterService() {
        return new DefaultCostCenterService();
    }

}
