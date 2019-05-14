package com.sap.csc.employeecreationbe.config;

import static com.sap.csc.employeecreationbe.config.Constants.INVALID_EMAIL_ADDRESS;
import static com.sap.csc.employeecreationbe.config.Constants.MISSING_COMPANY_CODE;
import static com.sap.csc.employeecreationbe.config.Constants.MISSING_EXTERNAL_ID;
import static com.sap.csc.employeecreationbe.config.Constants.MISSING_FIRST_NAME;
import static com.sap.csc.employeecreationbe.config.Constants.MISSING_GENDER;
import static com.sap.csc.employeecreationbe.config.Constants.MISSING_LAST_NAME;
import static com.sap.csc.employeecreationbe.config.Constants.MISSING_PERSONAL_INFORMATION;
import static com.sap.csc.employeecreationbe.config.Constants.MISSING_START_DATE;
import static com.sap.csc.employeecreationbe.config.Constants.MISSING_WORK_INFORMATION;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import com.sap.csc.employeecreationbe.exceptions.SapBadRequestException;
import com.sap.csc.employeecreationbe.exceptions.SapConflictException;
import com.sap.csc.employeecreationbe.model.employee.Employee;
import com.sap.csc.employeecreationbe.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ControllerAspect {

    private static final String USERNAME_REGEX = "^(?!.*([idscp])([0-9]){5,9})[a-z0-9]+[a-z0-9.-_]*$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    
    private final UserRepository userRepo;

    @Autowired
    public ControllerAspect(UserRepository userRepository) {
        this.userRepo = userRepository;
    }

    @AfterThrowing(value = "execution(* *..repository.Cloud*.*(..))", throwing = "e")
    public void report(JoinPoint jp, RestClientException e) {
        log.error("Exception in Repository...", e);
        log.error("Exception in method {}", jp);
    }

    @Before("execution(* com.sap.csc.employeecreationbe.controller.*Controller.create*(*))")
    public void logCreate(JoinPoint joinPoint) {
        log.info("Create endpoint called...");
        log.info("Create endpoint called with payload = {}", Arrays.asList(joinPoint.getArgs()));
    }

    @After("execution(* com.sap.csc.employeecreationbe.controller.*Controller.create*(..))")
    public void logAfterCreate() {
        log.info("Creation finished...");
    }

    @Before("execution(* com.sap.csc.employeecreationbe.service.businessuser.BusinessUserScheduler.assignBusinessRoles())")
	public void logBeforeScheduledBusinessRoleAssignment() {
	    log.info("Assigning business roles started...");
	}

	@After("execution(* com.sap.csc.employeecreationbe.service.businessuser.BusinessUserScheduler.assignBusinessRoles())")
	public void logAfterScheduledBusinessRoleAssignment() {
	    log.info("Assigning business roles finished...");
	}

	@Before(value = "execution(* com.sap.csc.employeecreationbe.controller.EmployeeController.createAll(*)) && " +
	        " args(employees)")
	public void validateCreateMultiple(List<Employee> employees) {
	    employees.forEach(this::validateCreate);
	}

	@Before(value = "execution(* com.sap.csc.employeecreationbe.controller.EmployeeController.create(*)) && " +
            " args(employee)")
    public void validateCreate(Employee employee) {
        if (isNull(employee.getExternalId()) || employee.getExternalId().isEmpty()) {
            log.error(MISSING_EXTERNAL_ID);
            throw SapBadRequestException.create(MISSING_EXTERNAL_ID);
        }

        if (isNull(employee.getWorkInformation())) {
            throw SapBadRequestException.create(MISSING_WORK_INFORMATION, employee.getExternalId());
        }

        if (isNull(employee.getPersonalInformation())) {
            throw SapBadRequestException.create(MISSING_PERSONAL_INFORMATION, employee.getExternalId());
        }

        if (isNull(employee.getPersonalInformation().getFirstName()) || employee.getPersonalInformation().getFirstName().isEmpty()) {
            log.error(MISSING_FIRST_NAME);
            throw SapBadRequestException.create(MISSING_FIRST_NAME, employee.getExternalId());
        }
        
        if (isNull(employee.getPersonalInformation().getLastName()) || employee.getPersonalInformation().getLastName().isEmpty()) {
            log.error(MISSING_LAST_NAME);
            throw SapBadRequestException.create(MISSING_LAST_NAME, employee.getExternalId());
        }

        if (isNull(employee.getWorkInformation().getCompanyCode()) || employee.getWorkInformation().getCompanyCode().isEmpty()) {
            log.error(MISSING_COMPANY_CODE);
            throw SapBadRequestException.create(MISSING_COMPANY_CODE, employee.getExternalId());
        }

        if (isNull(employee.getWorkInformation().getStartDate())) {
            log.error(MISSING_START_DATE);
            throw SapBadRequestException.create(MISSING_START_DATE, employee.getExternalId());
        }
        
        if (isNull(employee.getPersonalInformation().getGender())) {
            log.error(MISSING_GENDER);
            throw SapBadRequestException.create(MISSING_GENDER, employee.getExternalId());
        }
        
        if (nonNull(employee.getWorkInformation().getEmail())) {
            if (!employee.getWorkInformation().getEmail().isEmpty()) {
                if (!employee.getWorkInformation().getEmail().matches(EMAIL_REGEX)) {
                    throw SapBadRequestException.create(INVALID_EMAIL_ADDRESS, employee.getExternalId());
                }
                
                if (emailExists(employee)) {
                    throw SapConflictException.create("Email: " + employee.getWorkInformation().getEmail() +
                            " already exists!", employee.getExternalId());
                }
            }
        }
        
        if (nonNull(employee.getUserName())) {
            if (!employee.getUserName().isEmpty()) {
                if (userNameExists(employee)) {
                    throw SapConflictException.create("Username: " + employee.getUserName() +
                            " already exists!", employee.getExternalId());
                }

                if (!employee.getUserName().matches(USERNAME_REGEX)) {
                    throw SapBadRequestException.create("Invalid user name format! Allowed format: " + USERNAME_REGEX,
                            employee.getExternalId());
                }
            }
        }
    }

    private Boolean userNameExists(Employee employee) {
        return userRepo.findByUserName(employee.getUserName());
    }

    private Boolean emailExists(Employee employee) {
        return userRepo.findByEmail(employee.getWorkInformation().getEmail());
    }
    
}
