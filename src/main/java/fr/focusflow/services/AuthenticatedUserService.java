package fr.focusflow.services;

import fr.focusflow.entities.User;
import fr.focusflow.repositories.UserRepository;
import fr.focusflow.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserService {

    private final UserRepository userRepository;

    public AuthenticatedUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        return currentUser.getUsername();
    }

    /**
     * Retourne l'utilisateur connecté
     *
     * @return un objet User
     */
    public User getAuthenticatedUser() {
        String email = getAuthenticatedUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not Found !"));
    }
}
