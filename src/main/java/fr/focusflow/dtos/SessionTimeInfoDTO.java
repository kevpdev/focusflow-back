package fr.focusflow.dtos;

import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record SessionTimeInfoDTO(Long sessionId,
                                 LocalDateTime sessionStart,
                                 LocalDateTime sessionEnd,
                                 LocalDateTime remainingTime) {
}
