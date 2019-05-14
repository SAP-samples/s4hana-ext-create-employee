package com.sap.csc.employeecreationbe.config.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sap.csc.employeecreationbe.model.employee.enums.Language;

import java.io.IOException;

public class LanguageSerializer extends JsonSerializer<Language> {
	
    @Override
    public void serialize(Language value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getCode());
    }
    
}
