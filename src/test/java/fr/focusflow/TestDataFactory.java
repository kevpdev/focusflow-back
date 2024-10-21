package fr.focusflow;

import fr.focusflow.entities.*;
import fr.focusflow.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Classe de jeu de données pour les TU/TI
 */
public class TestDataFactory {

    /**
     * Création d'un utilisateur avec le rôle user
     *
     * @return un objet utilisateur
     */
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

    /**
     * Création d'un utilisateur avec le role admin
     *
     * @return un objet User
     */
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

    /**
     * Création d'une tâche
     *
     * @param id
     * @param user
     * @return un objet Task
     */
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

    /**
     * Creation d'une bodyRequest pour une tâche
     *
     * @return un objet Task
     */
    public static Task createRequestBodyTask() {
        return Task.builder()
                .title("Faire la vaisselle")
                .description("Faire la vaisselle avant samedi")
                .priority(2)
                .user(createUser())
                .status(ETaskStatus.PENDING)
                .build();
    }


    /**
     * Création d'une liste de deux tâches
     *
     * @return une liste de tâches
     */
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

    /**
     * Création d'un objet FocusSession
     *
     * @param id
     * @return objet FocusSession
     */
    public static FocusSession createFocusSession(Long id) {
        User user = createUser();
        Task task = createTask(1L, user);
        return FocusSession.builder()
                .user(user)
                .task(task)
                .id(id)
                .status(EFocusSessionStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
