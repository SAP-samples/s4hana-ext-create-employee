package com.sap.csc.employeecreationbe.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sap.csc.employeecreationbe.model.business.BusinessUser;

@Repository
public interface BusinessUserRepository extends JpaRepository<BusinessUser, Long> {

    void deleteByUserNameIgnoreCase(String userName);
    
}
