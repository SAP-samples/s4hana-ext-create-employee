package com.sap.csc.employeecreationbe.repository;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.CompanyCodeService;
import com.sap.csc.employeecreationbe.config.Constants;
import com.sap.csc.employeecreationbe.exceptions.SapException;
import com.sap.csc.employeecreationbe.model.employee.information.CompanyCode;
import com.sap.csc.employeecreationbe.repository.base.BaseRepository;

@Component
public class SdkCompanyCodeRepository implements BaseRepository<CompanyCode, String> {

	private final CompanyCodeService compCodeService;

	@Autowired
	public SdkCompanyCodeRepository(CompanyCodeService companyCodeService) {
		this.compCodeService = companyCodeService;
	}

	/**
	 * Calls {@code GET API_COMPANYCODE_SRV/CompanyCode} through
	 * {@link CompanyCodeService} of SAP S/4HANA SDK's Virtual Data Model to
	 * get CompanyCode by Key
	 * 
	 * @return the CompanyCode
	 * 
	 * @see CompanyCodeService#getCompanyCodeByKey
	 * @see <a href=
	 *      "https://api.sap.com/api/API_COMPANYCODE_SRV/resource">
	 *      SAP API Business Hub</a> for details of
	 *      {@code GET API_COMPANYCODE_SRV/CompanyCode} endpoint
	 */
	@Override
	public CompanyCode findOne(String s) {
		try {
			final com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.companycode.CompanyCode value = compCodeService
					.getCompanyCodeByKey(s)
					.execute(erp());
			
			return toCompCode(value);
		} catch (ODataException e) {
			throw SapException.create(e.getMessage());
		}
	}

	/**
	 * Calls {@code GET API_COMPANYCODE_SRV/CompanyCode} through
	 * {@link CompanyCodeService} of SAP S/4HANA SDK's Virtual Data Model to
	 * get All CompanyCode
	 * 
	 * @return All the CompanyCode
	 * 
	 * @see CompanyCodeService#getAllCompanyCode
	 * @see <a href=
	 *      "https://api.sap.com/api/API_COMPANYCODE_SRV/resource">
	 *      SAP API Business Hub</a> for details of
	 *      {@code GET API_COMPANYCODE_SRV/CompanyCode} endpoint
	 */
	@Override
	public List<CompanyCode> findAll() {
		try {
			return compCodeService.getAllCompanyCode()
					.execute(erp())
					.stream()
					.map(SdkCompanyCodeRepository::toCompCode)
					.collect(toList());
		} catch (ODataException e) {
			throw SapException.create(e.getMessage());
		}
	}

	private static CompanyCode toCompCode(com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.companycode.CompanyCode value) {
		return CompanyCode.builder()
				.addressId(value.getAddressID())
				.chartOfAccounts(value.getChartOfAccounts())
				.city(value.getCityName())
				.code(value.getCompanyCode())
				.company(value.getCompany())
				.controllingArea(value.getControllingArea())
				.country(value.getCountry())
				.countryChartOfAccounts(value.getCountryChartOfAccounts())
				.creditControllingArea(value.getCreditControlArea())
				.currency(value.getCurrency())
				.fieldStatusVariant(value.getFieldStatusVariant())
				.financialManagementArea(value.getFinancialManagementArea())
				.fiscalYearVariant(value.getFiscalYearVariant())
				.language(value.getLanguage())
				.name(value.getCompanyCodeName())
				.vatRegistration(value.getVATRegistration())
				.taxableEntity(value.getTaxableEntity())
				.nonTaxableTransactionTaxCode(value.getNonTaxableTransactionTaxCode())
				.build();
	}

	private static ErpConfigContext erp() {
		return new ErpConfigContext(Constants.REPLICATE_WORKFORCE);
	}
	
}
