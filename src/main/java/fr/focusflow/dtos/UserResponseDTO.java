package fr.focusflow.dtos;

import java.util.List;

public record UserResponseDTO(String message, String email, List<String> roles) {
}
