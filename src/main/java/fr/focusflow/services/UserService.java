package fr.focusflow.services;

import fr.focusflow.entities.User;

import java.util.Optional;

public interface UserService {

    User save(User newUser);

    boolean existByEmail(String email);

    Optional<User> findByEmail(String email);
}
