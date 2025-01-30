package fr.focusflow.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;

//@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Gestion des dates Java 8+
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Pas de timestamps numériques
        mapper.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC par défaut
        return mapper;
    }
}

