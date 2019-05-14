package com.sap.csc.employeecreationbe.repository;

import org.springframework.stereotype.Service;

import com.sap.csc.employeecreationbe.model.idp.User;

@Service
public interface UserRepository {

    Boolean findByEmail(String email);
    
    Boolean findByUserName(String userName);
    
    void create(User user);

}
