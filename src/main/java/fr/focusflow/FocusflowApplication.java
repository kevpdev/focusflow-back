package fr.focusflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FocusflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(FocusflowApplication.class, args);
    }

//    @Bean
//    @Primary
//    public ObjectMapper objectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule()); // Gestion des dates Java 8+
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Pas de timestamps numériques
//        mapper.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC par défaut
//        return mapper;
//    }

}
