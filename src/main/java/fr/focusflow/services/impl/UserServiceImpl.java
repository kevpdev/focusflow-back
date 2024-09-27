package fr.focusflow.services.impl;

import fr.focusflow.Models.User;
import fr.focusflow.repositories.UserRepository;
import fr.focusflow.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(String email, String password) {
        User newUser = new User(email, passwordEncoder.encode(password));
        return userRepository.save(newUser);
    }

    @Override
    public boolean existByEmail(String email) {
        return userRepository.existByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
