package com.sap.csc.employeecreationbe.repository.base;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface BaseRepository<T, ID> {

    T findOne(ID id);
    
    List<T> findAll();
    
}
