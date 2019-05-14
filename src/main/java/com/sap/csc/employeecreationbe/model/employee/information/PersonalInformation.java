package com.sap.csc.employeecreationbe.model.employee.information;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sap.csc.employeecreationbe.model.employee.enums.AcademicTitle;
import com.sap.csc.employeecreationbe.model.employee.enums.Gender;
import com.sap.csc.employeecreationbe.model.employee.enums.Language;
import com.sap.csc.employeecreationbe.model.employee.enums.NamePrefix;
import com.sap.csc.employeecreationbe.model.employee.enums.NameSupplement;
import com.sap.csc.employeecreationbe.model.employee.enums.Salutation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonalInformation {

    private String firstName;
    private String lastName;
    private String middleName;
    private String additionalLastName;
    private String birthName;
    private String nickName;
    private String initials;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Gender gender;

    @JsonIgnore
    private String fullName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Language language;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Salutation formOfAddress;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private AcademicTitle academicTitle;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private AcademicTitle academicSecondTitle;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private NamePrefix lastNamePrefix;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private NamePrefix lastNameSecondPrefix;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private NameSupplement nameSupplement;

    public String getFullName() {
        return firstName + " " + lastName;
    }
    
}
