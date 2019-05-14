package com.sap.csc.employeecreationbe.model.employee.information;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sap.csc.employeecreationbe.json.JsonDateDeserializer;
import com.sap.csc.employeecreationbe.json.JsonDateSerializer;
import com.sap.csc.employeecreationbe.model.employee.enums.WorkAgreementType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkInformation {
	
    private String companyCode;
    private String costCenter;
    private String email;
    private String roomNumber;
    private String building;
    private PhoneInformation phone;

    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private LocalDate startDate;

    @JsonDeserialize(using = JsonDateDeserializer.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    private LocalDate endDate;

    @JsonIgnore
    private WorkAgreementType agreementType;

}

