package com.sap.csc.employeecreationbe.repository.base;

import java.util.List;
import java.util.function.Predicate;

public interface CustomRepository<T, ID> extends BaseRepository<T, ID> {

    List<T> filterBy(Predicate<T> condition);
    
}
