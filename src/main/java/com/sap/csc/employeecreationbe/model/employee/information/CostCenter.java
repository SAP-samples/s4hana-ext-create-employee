package com.sap.csc.employeecreationbe.model.employee.information;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CostCenter {
	
    private String code;
    private String controllingArea;
    private String language;
    private String name;
    private String description;
    
}
