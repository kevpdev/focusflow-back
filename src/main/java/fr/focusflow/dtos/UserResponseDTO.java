package fr.focusflow.dtos;

import org.springframework.security.web.csrf.CsrfToken;

import java.util.List;

public record UserResponseDTO(String message, String email, List<String> roles, CsrfToken csrfToken) {
}
