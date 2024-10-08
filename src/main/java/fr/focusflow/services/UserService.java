package fr.focusflow.services;

import fr.focusflow.models.User;

import java.util.Optional;

public interface UserService {

    User save(User newUser);

    boolean existByEmail(String email);

    Optional<User> findByEmail(String email);
}
