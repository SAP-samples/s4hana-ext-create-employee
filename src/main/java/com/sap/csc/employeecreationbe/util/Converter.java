package com.sap.csc.employeecreationbe.util;

import org.springframework.stereotype.Service;

@Service
public interface Converter<T, U> {

    U convert(T value);
    
}
