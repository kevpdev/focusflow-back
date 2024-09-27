package fr.focusflow.dtos;

import lombok.Data;

@Data
public class UserResponseDTO {

    String token;

    public UserResponseDTO(String token) {
        this.token = token;
    }
}
