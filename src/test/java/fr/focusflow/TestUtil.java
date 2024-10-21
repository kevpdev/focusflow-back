package fr.focusflow;

import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Classe utilitaire pour les tests
 */
public class TestUtil {


    /**
     * Transforme un objet en Json
     *
     * @param objectValue
     * @return un Json au format String
     */
    public static String objectToJsonMapper(Object objectValue) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(objectValue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
