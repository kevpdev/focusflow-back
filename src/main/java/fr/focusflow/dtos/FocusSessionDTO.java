package fr.focusflow.dtos;

import fr.focusflow.entities.EStatus;
import lombok.Builder;

import java.time.ZonedDateTime;

public record FocusSessionDTO(
        Long id,
        Long userId,
        ZonedDateTime sessionStart,
        ZonedDateTime sessionEnd,
        EStatus status,
        Long taskId,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {

    @Builder
    public static FocusSessionDTO create(
            Long id,
            Long userId,
            ZonedDateTime sessionStart,
            ZonedDateTime sessionEnd,
            EStatus status,
            Long taskId,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt
    ) {
        return new FocusSessionDTO(id, userId, sessionStart, sessionEnd, status, taskId, createdAt, updatedAt);
    }
}

