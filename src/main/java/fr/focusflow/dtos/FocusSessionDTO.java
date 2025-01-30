package fr.focusflow.dtos;

import fr.focusflow.entities.EStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public record FocusSessionDTO(
        Long id,
        Long userId,
        LocalDateTime sessionStart,
        LocalDateTime sessionEnd,
        EStatus status,
        Long taskId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    @Builder
    public static FocusSessionDTO create(
            Long id,
            Long userId,
            LocalDateTime sessionStart,
            LocalDateTime sessionEnd,
            EStatus status,
            Long taskId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new FocusSessionDTO(id, userId, sessionStart, sessionEnd, status, taskId, createdAt, updatedAt);
    }
}

