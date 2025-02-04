package fr.focusflow;

import fr.focusflow.dtos.FocusSessionDTO;
import fr.focusflow.dtos.TaskDTO;
import fr.focusflow.entities.*;
import fr.focusflow.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Classe de jeu de données pour les TU/TI
 */
public class TestDataFactory {


    private static User createUser(Long id, String username, String email, ERole role) {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .roles(Set.of(Role.builder()
                        .id(role == ERole.ADMIN ? 1L : 2L)
                        .name(role.name())
                        .build()))
                .build();
    }

    /**
     * Création d'un utilisateur avec le rôle user
     *
     * @return un objet utilisateur
     */
    public static User createUser() {
        return createUser(2L, "toto", "toto@gmail.com", ERole.USER);
    }

    /**
     * Création d'un utilisateur avec le rôle admin
     *
     * @return un objet utilisateur
     */
    public static User createAdminUser() {
        return createUser(1L, "admin", "admin@gmail.com", ERole.ADMIN);
    }

    /**
     * Création d'une tâche
     *
     * @param id
     * @param user
     * @return un objet Task
     */
    // 🔹 Générique pour les tâches
    public static Task createTask(Long id, String title, String description, EStatus status, int priority, User user) {
        return Task.builder()
                .id(id)
                .title(title)
                .description(description)
                .priority(priority)
                .status(status)
                .user(user)
                .build();
    }

    public static Task createDefaultTask(Long id, User user) {
        return createTask(id, "Faire la vaisselle", "Faire la vaisselle avant samedi", EStatus.PENDING, 2, user);
    }

    /**
     * Création d'une liste de deux tâches
     *
     * @return une liste de tâches
     */
    public static List<Task> createTaskList() {
        User user = createUser();
        return List.of(
                createDefaultTask(1L, user),
                createTask(2L, "Ranger la chambre", "Ranger la chambre le dimanche", EStatus.PENDING, 2, user)
        );
    }

    /**
     * Creation d'une bodyRequest pour une tâche
     *
     * @return un objet Task
     */
    public static Task createRequestBodyTask() {
        return createDefaultTask(null, createUser());
    }

    /**
     * Conversion d'un objet Task en TaskDTO
     *
     * @param task l'entité Task
     * @return l'objet TaskDTO
     */
    public static TaskDTO mapTaskToTaskDTO(Task task) {
        if (task == null) {
            return null;
        }

        return TaskDTO.create(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getUser() != null ? task.getUser().getId() : null
        );
    }

    /**
     * Création directe d'un TaskDTO avec des données par défaut
     *
     * @param id l'identifiant de la tâche
     * @return un objet TaskDTO
     */
    public static TaskDTO createTaskDTO(Long id) {
        return TaskDTO.create(
                id,
                "Faire la vaisselle",
                "Faire la vaisselle avant samedi",
                EStatus.PENDING,
                2,
                ZonedDateTime.now().plusDays(3),
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                createUser().getId()
        );
    }

    /**
     * Création d'un TaskDTO avec un ID et un statut spécifique.
     *
     * @param id     l'identifiant de la tâche
     * @param status le statut personnalisé de la tâche
     * @return un objet TaskDTO avec l'ID et le statut spécifié
     */
    public static TaskDTO createTaskDTO(Long id, EStatus status) {
        return TaskDTO.create(
                id,
                "Faire la vaisselle",
                "Faire la vaisselle avant samedi",
                status,            // Statut personnalisé
                2,                 // Priorité par défaut
                ZonedDateTime.now().plusDays(3),
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                createUser().getId()
        );
    }


    /**
     * Création d'une liste de deux TaskDTO
     *
     * @return une liste de deux TaskDTO
     */
    public static List<TaskDTO> createTaskDTOList() {
        return List.of(
                createTaskDTO(1L),
                createTaskDTO(2L)
        );
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
     * Création d'un objet FocusSession à partir d'un objet DTO
     *
     * @param focusSessionDTO DTO contenant les données de la session
     * @return objet FocusSession
     */
    public static FocusSession createFocusSession(FocusSessionDTO focusSessionDTO) {
        return createFocusSession(focusSessionDTO, createUser());
    }

    /**
     * Création d'un objet FocusSession avec un utilisateur spécifique
     *
     * @param focusSessionDTO DTO contenant les données de la session
     * @param user            utilisateur associé à la session
     * @return objet FocusSession
     */
    public static FocusSession createFocusSession(FocusSessionDTO focusSessionDTO, User user) {
        return FocusSession.builder()
                .id(focusSessionDTO.id())
                .user(user)
                .task(createDefaultTask(focusSessionDTO.id(), user))
                .status(focusSessionDTO.status())
                .sessionStart(focusSessionDTO.sessionStart())
                .createdAt(focusSessionDTO.createdAt())
                .build();
    }

    /**
     * Création d'un objet FocusSessionDTO avec un statut par défaut (IN_PROGRESS)
     *
     * @param id       identifiant de la session
     * @param duration en minute
     * @return objet FocusSessionDTO
     */
    public static FocusSessionDTO createFocusSessionDTO(Long id, Long duration) {
        return createFocusSessionDTO(id, EStatus.IN_PROGRESS, duration);
    }

    /**
     * Création d'un objet FocusSessionDTO avec un statut par défaut (IN_PROGRESS)
     *
     * @param id       identifiant de la session
     * @param duration en minute
     * @param status   statut de la session
     * @return objet FocusSessionDTO
     */
    public static FocusSessionDTO createFocusSessionDTO(Long id, Long duration, EStatus status) {
        return createFocusSessionDTO(id, status, duration);
    }


    /**
     * Création d'un objet FocusSessionDTO avec un statut personnalisé
     *
     * @param id       identifiant de la session
     * @param status   statut de la session
     * @param duration en minute
     * @return objet FocusSessionDTO
     */
    public static FocusSessionDTO createFocusSessionDTO(Long id, EStatus status, Long duration) {
        User user = createUser();
        ZonedDateTime sessionStart = ZonedDateTime.now();
        ZonedDateTime sessionEnd = sessionStart.plusMinutes(duration);
        return FocusSessionDTO.builder()
                .id(id)
                .userId(user.getId())
                .taskId(1L)
                .status(status)
                .sessionStart(sessionStart)
                .sessionEnd(sessionEnd)
                .createdAt(ZonedDateTime.now())
                .build();
    }


}
