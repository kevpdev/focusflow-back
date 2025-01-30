package fr.focusflow.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "focus_sessions", schema = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FocusSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user; // Référence à l'utilisateur

    @Column(name = "session_start", nullable = false)
    private LocalDateTime sessionStart;

    @Column(name = "session_end")
    private LocalDateTime sessionEnd; // Peut être null si en cours

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EStatus status = EStatus.IN_PROGRESS; // Valeurs possibles : PENDING, IN_PROGRESS, CANCELED, DONE

    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id", nullable = false)
    private Task task; // Tâche associée à cette session

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Méthode appelée automatiquement avant l'insertion dans la base
    @PrePersist
    protected void onCreate() {
        sessionStart = LocalDateTime.now();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // Méthode appelée automatiquement avant chaque mise à jour
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

