package fr.focusflow.dtos;

import fr.focusflow.entities.EStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskDTO(
        Long id,
        String title,
        String description,
        EStatus status,
        Integer priority,
        LocalDate dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long userId
) {

    @Builder
    public static TaskDTO create(
            Long id,
            String title,
            String description,
            EStatus status,
            Integer priority,
            LocalDate dueDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long userId) {
        return new TaskDTO(id, title, description, status, priority, dueDate, createdAt, updatedAt, userId);
    }
}


