package fr.focusflow;

import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtil {


    public static String objectToJsonMapper(Object objectValue) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(objectValue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
