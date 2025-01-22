package fr.focusflow.dtos;

import fr.focusflow.entities.EStatus;
import lombok.Builder;

import java.time.ZonedDateTime;

public record TaskDTO(
        Long id,
        String title,
        String description,
        EStatus status,
        Integer priority,
        ZonedDateTime dueDate,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        Long userId
) {

    @Builder
    public static TaskDTO create(
            Long id,
            String title,
            String description,
            EStatus status,
            Integer priority,
            ZonedDateTime dueDate,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt,
            Long userId) {
        return new TaskDTO(id, title, description, status, priority, dueDate, createdAt, updatedAt, userId);
    }
}


