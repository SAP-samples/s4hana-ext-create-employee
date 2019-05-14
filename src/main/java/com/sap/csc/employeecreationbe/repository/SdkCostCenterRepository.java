package com.sap.csc.employeecreationbe.repository;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.costcenter.CostCenterText;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.CostCenterService;
import com.sap.csc.employeecreationbe.config.Constants;
import com.sap.csc.employeecreationbe.exceptions.SapException;
import com.sap.csc.employeecreationbe.exceptions.SapNotImplementedException;
import com.sap.csc.employeecreationbe.model.employee.information.CostCenter;
import com.sap.csc.employeecreationbe.repository.base.CustomRepository;

@Component
public class SdkCostCenterRepository implements CustomRepository<CostCenter, String> {
	
    private final CostCenterService costCenterService;

    @Autowired
    public SdkCostCenterRepository(CostCenterService costCenterService) {
        this.costCenterService = costCenterService;
    }

    @Override
	public CostCenter findOne(String s) {
	    throw SapNotImplementedException.create("Not implemented");
	}

	/**
     * @see SdkCostCenterRepository#findAll
     * 
	 * @return all the CostCenters after filtering based on the specified
	 *         {@code condition}
	 */
	@Override
	public List<CostCenter> filterBy(Predicate<CostCenter> condition) {
	    return findAll().stream()
	            .filter(condition)
	            .collect(toList());
	}

	/**
	 * Calls {@code GET API_COSTCENTER_SRV/CostCenter} through
	 * {@link CostCenterService} of SAP S/4HANA Cloud SDK's Virtual Data Model to get
	 * All CostCenters
	 * 
	 * @return All the CostCenter
	 * 
	 * @see CostCenterService#getAllCostCenterText
	 * @see <a href= "https://api.sap.com/api/API_COSTCENTER_SRV/resource"> SAP
	 *      API Business Hub</a> for details of
	 *      {@code GET API_COSTCENTER_SRV/A_CostCenterText} OData API endpoint
	 */
    @Override
    public List<CostCenter> findAll() {
        try {
            return costCenterService.getAllCostCenterText().execute(erp())
                    .stream()
                    .map(SdkCostCenterRepository::toCostCenter)
                    .collect(toList());
        } catch (ODataException e) {
            throw SapException.create(e.getMessage());
        }
        
    }

    private static CostCenter toCostCenter(CostCenterText costCenter) {
        return CostCenter.builder()
                .code(costCenter.getCostCenter())
                .controllingArea(costCenter.getControllingArea())
                .description(costCenter.getCostCenterDescription())
                .language(costCenter.getLanguage())
                .name(costCenter.getCostCenterName())
                .build();
    }

    private static ErpConfigContext erp() {
        return new ErpConfigContext(Constants.REPLICATE_WORKFORCE);
    }
    
}
