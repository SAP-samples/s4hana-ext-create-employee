package com.sap.csc.employeecreationbe.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sap.csc.employeecreationbe.config.json.GenderSerializer;
import com.sap.csc.employeecreationbe.config.json.LanguageSerializer;
import com.sap.csc.employeecreationbe.exceptions.SapException;
import com.sap.csc.employeecreationbe.model.employee.Employee;
import com.sap.csc.employeecreationbe.model.employee.enums.Gender;
import com.sap.csc.employeecreationbe.model.employee.enums.Language;
import com.sap.csc.employeecreationbe.model.employee.information.PersonalInformation;
import com.sap.csc.employeecreationbe.model.employee.information.WorkInformation;
import com.sap.csc.employeecreationbe.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.time.LocalDate;

import static com.sap.csc.employeecreationbe.config.Constants.*;
import static com.sap.csc.employeecreationbe.model.employee.enums.Gender.FEMALE;
import static com.sap.csc.employeecreationbe.model.employee.enums.Language.EN;
import static com.sap.csc.employeecreationbe.model.employee.enums.WorkAgreementType.WORKFORCE_PERSON;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("Create employee with ")
class EmployeeControllerTest {

    private static final String EMPLOYEE_URI = "/employee";
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository useRepo;

    @Test
    @DisplayName("empty external id returns bad request.")
    void createEmployeeWithoutExternalId() throws Exception {
        final Employee requestEmployee = getMockEmployee();
        requestEmployee.setExternalId("");
       
        mockMvc.perform(post(EMPLOYEE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJsonString(requestEmployee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Missing external id")));
    }

    @Test
    @DisplayName("null external id returns bad request.")
    void createEmployeeWithoutExternalId2() throws Exception {
        final Employee requestEmployee = getMockEmployee();
        requestEmployee.setExternalId(null);

        mockMvc.perform(post(EMPLOYEE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJsonString(requestEmployee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MISSING_EXTERNAL_ID)));
    }

    @Test
    @DisplayName("null first name returns bad request.")
    void createWithoutFirstName() throws Exception {
        final Employee requestEmployee = getMockEmployee();
        requestEmployee.getPersonalInformation().setFirstName("");
        
        mockMvc.perform(post(EMPLOYEE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJsonString(requestEmployee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MISSING_FIRST_NAME)));
    }

    @Test
    @DisplayName("empty first name returns bad request.")
    void createWithoutFirstName2() throws Exception {
        final Employee requestEmployee = getMockEmployee();
        requestEmployee.getPersonalInformation().setFirstName(null);
        
        mockMvc.perform(post(EMPLOYEE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJsonString(requestEmployee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MISSING_FIRST_NAME)));
    }

    @Test
    @DisplayName("null last name returns bad request.")
    void createWithoutLastName() throws Exception {
        final Employee requestEmployee = getMockEmployee();
        requestEmployee.getPersonalInformation().setLastName(null);
        
        mockMvc.perform(post(EMPLOYEE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJsonString(requestEmployee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MISSING_LAST_NAME)));
    }

    @Test
    @DisplayName("empty last name returns bad request.")
    void createWithoutLastName2() throws Exception {
        final Employee requestEmployee = getMockEmployee();
        requestEmployee.getPersonalInformation().setLastName("");
        
        mockMvc.perform(post(EMPLOYEE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJsonString(requestEmployee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MISSING_LAST_NAME)));
    }

    @Test
    @DisplayName("empty company code returns bad request.")
    void createWithoutCompanyCode() throws Exception {
        final Employee requestEmployee = getMockEmployee();
        requestEmployee.getWorkInformation().setCompanyCode("");
        
        mockMvc.perform(post(EMPLOYEE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJsonString(requestEmployee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MISSING_COMPANY_CODE)));
    }

    @Test
    @DisplayName("null company code returns bad request.")
    void createWithoutCompanyCode2() throws Exception {
        final Employee requestEmployee = getMockEmployee();
        requestEmployee.getWorkInformation().setCompanyCode(null);
        
        mockMvc.perform(post(EMPLOYEE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJsonString(requestEmployee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MISSING_COMPANY_CODE)));
    }

    @Test
    @DisplayName("missing start date returns bad request.")
    void createWithoutStartDate() throws Exception {
        final Employee requestEmployee = getMockEmployee();
        requestEmployee.getWorkInformation().setStartDate(null);
        
        mockMvc.perform(post(EMPLOYEE_URI)
                .contentType(APPLICATION_JSON)
                .content(toJsonString(requestEmployee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MISSING_START_DATE)));
    }

    @Test
    @DisplayName(" existing email returns bad request.")
    void createWithExistingMail() throws Exception {
        final Employee employee = getMockEmployee();

        final String email = "existingemail@email.com";
        employee.getWorkInformation().setEmail(email);
        
        when(useRepo.findByEmail(email)).thenReturn(true);

        mockMvc.perform(post(URI.create(EMPLOYEE_URI))
                .contentType(APPLICATION_JSON)
                .content(toJsonString(employee)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString(employee.getWorkInformation().getEmail() + " already exists")));
    }

    @Test
    @DisplayName(" existing user name returns bad request.")
    void createWithExistingUserName() throws Exception {
        final Employee employee = getMockEmployee();

        final String userName = "existingusername";
        employee.setUserName(userName);
        
        when(useRepo.findByUserName(userName)).thenReturn(true);

        mockMvc.perform(post(URI.create(EMPLOYEE_URI))
                .contentType(APPLICATION_JSON)
                .content(toJsonString(employee)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString(employee.getUserName() + " already exists")));
    }

    @Test
    @DisplayName(" missing work information returns bad request.")
    void createWithMissingWorkInformation() throws Exception {
        final Employee employee = getMockEmployee();

        employee.setWorkInformation(null);

        mockMvc.perform(post(URI.create(EMPLOYEE_URI))
                .contentType(APPLICATION_JSON)
                .content(toJsonString(employee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MISSING_WORK_INFORMATION)));
    }

    @Test
    @DisplayName(" missing personal information returns bad request.")
    void createWithMissingPersonalInformation() throws Exception {
        final Employee employee = getMockEmployee();
        employee.setPersonalInformation(null);

        mockMvc.perform(post(URI.create(EMPLOYEE_URI))
                .contentType(APPLICATION_JSON)
                .content(toJsonString(employee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MISSING_PERSONAL_INFORMATION)));
    }

    @Test
    @DisplayName(" invalid email address returns bad request.")
    void createWithInvalidEmailAddress() throws Exception {
        final Employee employee = getMockEmployee();
        employee.getWorkInformation().setEmail("asdf.co.");
        
        mockMvc.perform(post(URI.create(EMPLOYEE_URI))
                .contentType(APPLICATION_JSON)
                .content(toJsonString(employee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(INVALID_EMAIL_ADDRESS)));
    }

    @Test
    @DisplayName(" missing gender returns bad request.")
    void createWithMissingGender() throws Exception {
        final Employee employee = getMockEmployee();
        employee.getPersonalInformation().setGender(null);

        mockMvc.perform(post(URI.create(EMPLOYEE_URI))
                .contentType(APPLICATION_JSON)
                .content(toJsonString(employee)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(MISSING_GENDER)));
    }

    private static Employee getMockEmployee() {
        final WorkInformation workInformation = WorkInformation.builder()
                .companyCode("10123")
                .costCenter("1235123")
                .email("email@email.com")
                .startDate(LocalDate.now())
                .agreementType(WORKFORCE_PERSON)
                .build();

        final PersonalInformation personalInformation = PersonalInformation.builder()
                .firstName("testFirstname")
                .lastName("testLastName")
                .language(EN)
                .gender(FEMALE)
                .build();

        return Employee.builder()
                .userName("test")
                .externalId("TESTEXTERNALID")
                .workInformation(workInformation)
                .personalInformation(personalInformation)
                .build();
    }

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        SimpleModule module = new SimpleModule("CustomModule", new Version(1, 0, 0, "", "", ""));
        module.addSerializer(Language.class, new LanguageSerializer());
        module.addSerializer(Gender.class, new GenderSerializer());
        
        mapper.registerModule(module);
        
        return mapper;
    }

    private static String toJsonString(Object obj) {
        try {
            return getMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw SapException.create(e.getMessage());
        }
    }

}

