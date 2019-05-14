package com.sap.csc.employeecreationbe.model.employee;

import java.util.Set;

import com.sap.csc.employeecreationbe.model.employee.information.PersonalInformation;
import com.sap.csc.employeecreationbe.model.employee.information.WorkInformation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * If User Name is present then a business user is created.
 */
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Employee {

    private String externalId;
    private String userName;
    private PersonalInformation personalInformation;
    private WorkInformation workInformation;
    private Set<String> roles;

}
