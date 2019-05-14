package com.sap.csc.employeecreationbe.config.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sap.csc.employeecreationbe.model.employee.enums.Gender;

import java.io.IOException;

public class GenderSerializer extends JsonSerializer<Gender> {
	
    @Override
	public void serialize(Gender value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getKey());
    }
    
}
