package com.sap.csc.employeecreationbe.json;

import java.io.IOException;
import java.time.LocalDate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
        final String date = jsonparser.getText();
        
        return LocalDate.parse(date);
    }
    
}