package com.sap.csc.employeecreationbe.util.employee;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.sap.csc.employeecreationbe.model.employee.Employee;
import com.sap.csc.employeecreationbe.util.Converter;
import com.sap.xi.pasein.CLOSED_DatePeriod;
import com.sap.xi.pasein.ExternalID;
import com.sap.xi.pasein.GenderCode;
import com.sap.xi.pasein.LANGUAGEINDEPENDENT_LONG_Name;
import com.sap.xi.pasein.LANGUAGEINDEPENDENT_MEDIUM_Name;
import com.sap.xi.pasein.NOSC_UUID;
import com.sap.xi.pasein.PersonWorkAgreementType;
import com.sap.xi.pasein.PersonalInformation;
import com.sap.xi.pasein.UserName;
import com.sap.xi.pasein.WorkAgreementJobInformation;
import com.sap.xi.pasein.WorkAgreementStatus;
import com.sap.xi.pasein.WorkforcePersonMasterData;
import com.sap.xi.pasein.WorkforcePersonMasterDataWorkAgreementInformation;

@Component
public class EmployeeConverter implements Converter<Employee, WorkforcePersonMasterData> {

	@Override
	public WorkforcePersonMasterData convert(Employee employee) {
		final WorkforcePersonMasterData personMasterData = new WorkforcePersonMasterData();
		
		personMasterData.setPersonUUID(
				NOSC_UUID.Factory.fromString(UUID.randomUUID().toString(), NOSC_UUID.MY_QNAME.getNamespaceURI()));
		personMasterData.setPersonExternalID(
				ExternalID.Factory.fromString(employee.getExternalId(), ExternalID.MY_QNAME.getNamespaceURI()));
		personMasterData.setUserName(
				UserName.Factory.fromString(employee.getUserName(), UserName.MY_QNAME.getNamespaceURI()));

		final PersonalInformation personalInformation = new PersonalInformation();
		setPersonalInformation(employee, personalInformation);

		personMasterData.setPersonalInformation(personalInformation);

		final WorkforcePersonMasterDataWorkAgreementInformation workAgreementInformation = new WorkforcePersonMasterDataWorkAgreementInformation();
		workAgreementInformation.setPersonWorkAgreementExternalID(
				ExternalID.Factory.fromString(employee.getExternalId(), ExternalID.MY_QNAME.getNamespaceURI()));
		workAgreementInformation.setPersonWorkAgreementUUID(
				NOSC_UUID.Factory.fromString(UUID.randomUUID().toString(), NOSC_UUID.MY_QNAME.getNamespaceURI()));
		workAgreementInformation.setPersonWorkAgreementType(PersonWorkAgreementType.value1);

		final LocalDate endDate = Optional.ofNullable(employee.getWorkInformation().getEndDate())
				.orElse(LocalDate.parse("9999-12-31"));

		final CLOSED_DatePeriod validityPeriod = createValidityPeriod(employee.getWorkInformation().getStartDate(),
				endDate);
		
		final WorkAgreementJobInformation jobInformation = new WorkAgreementJobInformation();
		jobInformation.setValidityPeriod(validityPeriod);
		jobInformation.setWorkAgreementStatus(WorkAgreementStatus.value2);
		jobInformation.setCompanyCode(employee.getWorkInformation().getCompanyCode());
		jobInformation.setCostCenter(employee.getWorkInformation().getCostCenter());
		
		workAgreementInformation.addWorkAgreementJobInformation(jobInformation);
		personMasterData.addWorkAgreementInformation(workAgreementInformation);

		return personMasterData;
	}

	private static void setPersonalInformation(Employee employee, PersonalInformation personalInformation) {
		personalInformation.setFirstName(
				LANGUAGEINDEPENDENT_MEDIUM_Name.Factory.fromString(employee.getPersonalInformation().getFirstName(),
						LANGUAGEINDEPENDENT_MEDIUM_Name.MY_QNAME.getNamespaceURI()));
		personalInformation.setLastName(
				LANGUAGEINDEPENDENT_MEDIUM_Name.Factory.fromString(employee.getPersonalInformation().getLastName(),
						LANGUAGEINDEPENDENT_MEDIUM_Name.MY_QNAME.getNamespaceURI()));
		personalInformation.setPersonFullName(
				LANGUAGEINDEPENDENT_LONG_Name.Factory.fromString(employee.getPersonalInformation().getFullName(),
						LANGUAGEINDEPENDENT_LONG_Name.MY_QNAME.getNamespaceURI()));
		personalInformation.setGenderCode(GenderCode.Factory.fromString(
				employee.getPersonalInformation().getGender().getCode(), GenderCode.MY_QNAME.getNamespaceURI()));
	}

	private static CLOSED_DatePeriod createValidityPeriod(LocalDate startDate, LocalDate endDate) {
		final CLOSED_DatePeriod period = new CLOSED_DatePeriod();
		period.setStartDate(startDate.toString());
		period.setEndDate(endDate.toString());

		return period;
	}

}
