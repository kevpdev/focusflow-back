package fr.focusflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class FocusflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(FocusflowApplication.class, args);
	}

}
