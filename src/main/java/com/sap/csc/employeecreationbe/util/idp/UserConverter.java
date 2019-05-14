package com.sap.csc.employeecreationbe.util.idp;

import java.util.Collections;

import org.springframework.stereotype.Component;

import com.sap.csc.employeecreationbe.model.employee.Employee;
import com.sap.csc.employeecreationbe.model.idp.User;
import com.sap.csc.employeecreationbe.util.Converter;

@Component
public class UserConverter implements Converter<Employee, User> {
	
    @Override
    public User convert(Employee value) {
        return new User.UserBuilder()
                .setUserName(value.getUserName())
                .setSapUserName(value.getUserName())
                .setGivenName(value.getPersonalInformation().getFirstName())
                .setFamilyName(value.getPersonalInformation().getLastName())
                .setEmail(value.getWorkInformation().getEmail())
                .setScenarios(Collections.singletonList("SP_USER"))
                .setExternalId(value.getExternalId())
                .createUser();
    }
    
}
