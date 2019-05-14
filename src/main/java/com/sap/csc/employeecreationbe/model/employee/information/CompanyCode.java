package com.sap.csc.employeecreationbe.model.employee.information;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyCode {

    private String code;
    private String name;
    private String city;
    private String country;
    private String currency;
    private String language;
    private String chartOfAccounts;
    private String fiscalYearVariant;
    private String company;
    private String creditControllingArea;
    private String countryChartOfAccounts;
    private String financialManagementArea;
    private String addressId;
    private String taxableEntity;
    private String vatRegistration;
    private String controllingArea;
    private String fieldStatusVariant;
    private String nonTaxableTransactionTaxCode;
    
}
