package com.sap.csc.employeecreationbe.model.business;

import java.util.Collections;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * JPA is used only to temporarily persist information (username and roles)
 * about the created business user because of the delay produced by the SAP
 * S/4HANA system for Business User creation.
 * <p>
 * The persisted information is then used to update the Business User created in
 * the SAP S/4HANA system and afterwards is removed. Database is in-memory, free
 * of charge and can only store the data until the application is restarted.
 *
 */
@Data
@Entity
@RequiredArgsConstructor(staticName = "of")
public class BusinessUser {
	
	@Id
	@GeneratedValue
	private Long id;
	
    @Column
    private final String userName;

    @ElementCollection
    private final Set<String> businessRoles;

    private BusinessUser() {
        this.userName = "";
        this.businessRoles = Collections.emptySet();
    }

}
