package com.sap.csc.employeecreationbe.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface SoapClient<T, U> {

    U send(T value);
    
    List<U> sendAll(List<T> values);
    
}
