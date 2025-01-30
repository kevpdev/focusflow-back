package fr.focusflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

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

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())  // pour supporter les records
                .registerModule(new JavaTimeModule())        // pour les dates (LocalDate, LocalDateTime)
                .findAndRegisterModules();
        ;
        try {
            return objectMapper.writeValueAsString(objectValue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
