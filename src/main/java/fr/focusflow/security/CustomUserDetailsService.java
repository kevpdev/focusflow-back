package fr.focusflow.security;

import fr.focusflow.entities.User;
import fr.focusflow.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Récupérer le user
        Optional<User> optionalUser = userService.findByEmail(email);

        return optionalUser.map(user -> new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        )).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    }
}
