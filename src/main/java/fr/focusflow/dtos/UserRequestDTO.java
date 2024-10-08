package fr.focusflow.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
        @Email(message = "L'email doit être valide") String email,
        @NotBlank(message = "Le mot de passe ne peut pas être vide") String password,
        @NotBlank(message = "Le nom d'utilisateur ne peut pas être vide") String username) {
}
