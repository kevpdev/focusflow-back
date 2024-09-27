package fr.focusflow.services;

import fr.focusflow.Models.User;

import java.util.Optional;

public interface UserService {

    User save(String email, String password);

    boolean existByEmail(String email);

    Optional<User> findByEmail(String email);
}
