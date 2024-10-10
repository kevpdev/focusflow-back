package fr.focusflow;

import fr.focusflow.entities.*;
import fr.focusflow.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TestDataFactory {

    public static User createUser() {
        return User.builder()
                .id(2L)
                .username("toto")
                .email("toto@gmail.com")
                .roles(Set.of(Role.builder()
                        .id(2L)
                        .name(ERole.USER.name())
                        .build()))
                .build();
    }


    public static User createAdminUser() {
        return User.builder()
                .id(1L)
                .username("admin")
                .email("admin@gmail.com")
                .roles(Set.of(Role.builder()
                        .id(1L)
                        .name(ERole.ADMIN.name())
                        .build()))
                .build();
    }

    public static Task createTask(Long id, User user) {
        return Task.builder()
                .id(id)
                .title("Faire la vaisselle")
                .description("Faire la vaisselle avant samedi")
                .priority(2)
                .user(user)
                .status(ETaskStatus.PENDING)
                .build();
    }

    public static Task createRequestBodyTask() {
        return Task.builder()
                .title("Faire la vaisselle")
                .description("Faire la vaisselle avant samedi")
                .priority(2)
                .user(createUser())
                .status(ETaskStatus.PENDING)
                .build();
    }


    public static List<Task> createTaskList() {
        User user = createUser();
        Task task1 = createTask(1L, user);
        Task task2 = Task.builder()
                .id(2L)
                .title("Ranger la chambre")
                .description("Ranger la chambre le dimanche")
                .priority(2)
                .user(user)
                .build();
        return List.of(task1, task2);
    }


    /**
     * Simule un Security Contexte pour les donnees de session
     */
    public static void setUpSecurityContext() {
        User user = createUser();
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        CustomUserDetails customUserDetails = new CustomUserDetails(user.getId(), user.getEmail(), "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );
    }
}
