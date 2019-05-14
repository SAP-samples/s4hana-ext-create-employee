package com.sap.csc.employeecreationbe.util;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.csc.employeecreationbe.exceptions.SapException;

public interface Helper {
	
    ObjectMapper mapper = new ObjectMapper();

    static Function<String, JsonNode> mapToJson() {
        return s -> {
            try {
                return mapper.readTree(s);
            } catch (IOException e) {
                throw SapException.create(e.getMessage());
            }
        };
    }

    static <T> String mapToJsonString(T t) {
        try {
            return mapper.writeValueAsString(t);
        } catch (IOException e) {
            throw SapException.create(e.getMessage());
        }
    }

    static <T> Optional<T> wrapPossibleNull(T t) {
        return Optional.ofNullable(t);
    }

    static <T> Optional<T> wrap(T t) {
        return Optional.of(t);
    }
    
}
