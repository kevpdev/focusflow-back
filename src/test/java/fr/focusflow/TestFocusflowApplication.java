package fr.focusflow;

import org.springframework.boot.SpringApplication;

public class TestFocusflowApplication {

	public static void main(String[] args) {
		SpringApplication.from(FocusflowApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
