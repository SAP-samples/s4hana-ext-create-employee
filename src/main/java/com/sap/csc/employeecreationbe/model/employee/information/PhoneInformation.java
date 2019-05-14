package com.sap.csc.employeecreationbe.model.employee.information;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sap.csc.employeecreationbe.model.employee.enums.PhoneType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PhoneInformation {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private PhoneType type;
    
    private String countryDialingCode;
    private String phoneNumberAreaID;
    private String phoneNumber;
    private String phoneNumberExtension;
    
}
