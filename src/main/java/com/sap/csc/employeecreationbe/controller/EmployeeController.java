package com.sap.csc.employeecreationbe.controller;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sap.csc.employeecreationbe.model.business.BusinessUser;
import com.sap.csc.employeecreationbe.model.employee.Employee;
import com.sap.csc.employeecreationbe.model.employee.enums.AcademicTitle;
import com.sap.csc.employeecreationbe.model.employee.enums.Gender;
import com.sap.csc.employeecreationbe.model.employee.enums.Language;
import com.sap.csc.employeecreationbe.model.employee.enums.NamePrefix;
import com.sap.csc.employeecreationbe.model.employee.enums.NameSupplement;
import com.sap.csc.employeecreationbe.model.employee.enums.PhoneType;
import com.sap.csc.employeecreationbe.model.employee.enums.Salutation;
import com.sap.csc.employeecreationbe.model.employee.information.CompanyCode;
import com.sap.csc.employeecreationbe.model.employee.information.CostCenter;
import com.sap.csc.employeecreationbe.model.idp.User;
import com.sap.csc.employeecreationbe.model.soap.SeverityCode;
import com.sap.csc.employeecreationbe.model.soap.SoapResponse;
import com.sap.csc.employeecreationbe.repository.UserRepository;
import com.sap.csc.employeecreationbe.repository.base.BaseRepository;
import com.sap.csc.employeecreationbe.repository.base.CustomRepository;
import com.sap.csc.employeecreationbe.repository.jpa.BusinessUserRepository;
import com.sap.csc.employeecreationbe.service.SoapClient;
import com.sap.csc.employeecreationbe.util.Converter;
import com.sap.xi.pasein.LogItemNote;
import com.sap.xi.pasein.LogItemSeverityCode;
import com.sap.xi.pasein.NOSC_LogItem;
import com.sap.xi.pasein.NOSC_LogItemCategoryCode;
import com.sap.xi.pasein.WorkforcePersonMasterData;
import com.sap.xi.pasein.WorkforcePersonMasterDataReplConfLogMessage;
import com.sap.xi.pasein.WorkforcePersonMasterDataReplResponse;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

	private final SoapClient<WorkforcePersonMasterData, WorkforcePersonMasterDataReplResponse> employeeClient;
	
	private final Converter<Employee, WorkforcePersonMasterData> wfConverter;
	private final Converter<Employee, User> userConverter;
	
	private final BaseRepository<CompanyCode, String> compCodeRepo;
	private final CustomRepository<CostCenter, String> costCenterRepo;
	private final UserRepository userRepo;
	private final BusinessUserRepository businessUserRepo;

	@Autowired
	public EmployeeController(
			SoapClient<WorkforcePersonMasterData, WorkforcePersonMasterDataReplResponse> employeeClient,
			Converter<Employee, WorkforcePersonMasterData> converter,
			BaseRepository<CompanyCode, String> baseRepository, CustomRepository<CostCenter, String> customRepository,
			UserRepository userRepo, Converter<Employee, User> userConverter, BusinessUserRepository businessUserRepo) {
		
		this.employeeClient = employeeClient;
		this.wfConverter = converter;
		this.compCodeRepo = baseRepository;
		this.costCenterRepo = customRepository;
		this.userRepo = userRepo;
		this.userConverter = userConverter;
		this.businessUserRepo = businessUserRepo;
	}

	/**
	 * This method creates an employee in the SAP S/4HANA System and the
	 * corresponding user in its Identity Provider (IdP).
	 * <br>
	 * The method also assigns the roles to the employee.
	 */
	@PostMapping
	public ResponseEntity<SoapResponse> create(@RequestBody Employee employee) {
		final WorkforcePersonMasterData data = wfConverter.convert(employee);

		// Invoke the Employee SOAP client to send and create the Employee in the SAP S/4HANA system
		final WorkforcePersonMasterDataReplResponse response = employeeClient.send(data);
		final SoapResponse soapResponse = toSoapResponse(response);

		// If the creation of the employee is successful
		final HttpStatus status = soapResponse.getStatus();
		if (status.is2xxSuccessful()) {

			// Check the eligibility of the User Creation for the employee in IdP
			if (isEligibleForUserCreation((employee))) {
				
				// invoke UserRepository to create the user in IdP
				userRepo.create(userConverter.convert(employee));
			}

			// Assign the roles to the created employee if the business role(s) are selected
			final Set<String> roles = Optional.ofNullable(employee.getRoles()).orElse(Collections.emptySet());
			if (!roles.isEmpty()) {
				businessUserRepo.save(BusinessUser.of(employee.getUserName(), employee.getRoles()));
			}
		}

		return ResponseEntity.status(status).body(soapResponse);
	}

	private static SoapResponse toSoapResponse(WorkforcePersonMasterDataReplResponse response) {
		final NOSC_LogItem noscLogItem = Stream
				.of(Stream.of(response.getWorkforcePersonMasterDataReplConfLogMessage())
						.findFirst()
						.orElse(new WorkforcePersonMasterDataReplConfLogMessage()).getLogItem())
				.findFirst().orElse(defaultLog());

		return SoapResponse.builder()
				.externalId(response.getPersonExternalID().toString())
				.status(SeverityCode.valueOf(noscLogItem.getSeverityCode().toString()).getStatus())
				.message(noscLogItem.getNote().toString())
				.build();
	}

	private static NOSC_LogItem defaultLog() {
		final NOSC_LogItem noscLogItem = new NOSC_LogItem();
		
		noscLogItem.setNote(LogItemNote.Factory.fromString("default", LogItemNote.MY_QNAME.getNamespaceURI()));
		noscLogItem.setSeverityCode(
				LogItemSeverityCode.Factory.fromString("E", NOSC_LogItemCategoryCode.MY_QNAME.getNamespaceURI()));

		return noscLogItem;
	}

	private static boolean isEligibleForUserCreation(Employee employee) {
		final boolean nullFields = isNull(employee.getWorkInformation().getEmail())
				|| isNull(employee.getPersonalInformation().getLastName());
		
		if (!nullFields) {
			final boolean emptyFields = employee.getWorkInformation().getEmail().isEmpty()
					|| employee.getPersonalInformation().getLastName().isEmpty();

			return !emptyFields;
		}

		return false;
	}

	/**
	 * This method is used to create multiple employees in S4HANA system and the
	 * corresponding users in IDP This method also assigns the roles to the
	 * employees
	 */
	@PostMapping("/batch")
	public ResponseEntity<List<SoapResponse>> createAll(@RequestBody List<Employee> employees) {
		final List<WorkforcePersonMasterData> data = employees.stream()
				.map(wfConverter::convert)
				.collect(Collectors.toList());

		// Invoke the Employee SOAP client to send and create the Employees in the S/4HANA system
		final List<SoapResponse> response = employeeClient.sendAll(data).stream()
				.map(EmployeeController::toSoapResponse)
				.collect(toList());

		final HttpStatus status = response.stream()
				.map(SoapResponse::getStatus)
				.filter(HttpStatus::is2xxSuccessful)
				.findFirst()
				.orElse(HttpStatus.BAD_REQUEST);

		// Check for the successful creation of employees in S/4HANA System
		final Set<String> eligibleExtIds = response.stream()
				.filter(r -> r.getStatus().is2xxSuccessful())
				.map(SoapResponse::getExternalId)
				.collect(toSet());

		final List<Employee> eligibleEmployees = employees.stream()
				.filter(e -> eligibleExtIds.contains(e.getExternalId()))
				.collect(toList());

		if (status.is2xxSuccessful()) {
			// Iterating through all the created employees, check the eligibility of the
			// User Creation in IdP for each created employee
			// and create the corresponding user in IdP if the employee is eligible
			eligibleEmployees.stream()
					.filter(EmployeeController::isEligibleForUserCreation)
					.map(userConverter::convert)
					.forEach(userRepo::create);

			// Assign the roles to the created employee if the business role(s) are selected
			final List<BusinessUser> businessUsers = eligibleEmployees.stream()
					.filter(e -> !CollectionUtils.isEmpty(e.getRoles()))
					.map(e -> BusinessUser.of(e.getUserName(), e.getRoles()))
					.collect(toList());

			businessUserRepo.saveAll(businessUsers);
		}

		return ResponseEntity.status(status).body(response);
	}

	@GetMapping("/language")
	public List<Language> languages() {
		return Arrays.asList(Language.values());
	}

	@GetMapping("/gender")
	public List<Gender> genders() {
		return Arrays.asList(Gender.values());
	}

	@GetMapping("/academictitle")
	public List<AcademicTitle> academicTitles() {
		return Arrays.asList(AcademicTitle.values());
	}

	@GetMapping("/name/prefix")
	public List<NamePrefix> namePrefixes() {
		return Arrays.asList(NamePrefix.values());
	}

	@GetMapping("/name/supplement")
	public List<NameSupplement> supplements() {
		return Arrays.asList(NameSupplement.values());
	}

	@GetMapping("/phonetype")
	public List<PhoneType> phoneTypes() {
		return Arrays.asList(PhoneType.values());
	}

	@GetMapping("/salutation")
	public List<Salutation> salutations() {
		return Arrays.asList(Salutation.values());
	}

	@GetMapping("/companycode")
	public List<CompanyCode> companyCodes() {
		return compCodeRepo.findAll();
	}

	@GetMapping("/costcenter")
	public List<CostCenter> costCenters(@RequestParam String companyCode, @RequestParam String language) {
		final Predicate<CostCenter> code = c -> c.getCode().startsWith(companyCode);
		final Predicate<CostCenter> lang = c -> c.getLanguage().equalsIgnoreCase(language);

		return costCenterRepo.filterBy(code.and(lang));
	}

}
