package fr.focusflow.dtos;

import lombok.Builder;

import java.time.ZonedDateTime;


@Builder
public record SessionTimeInfoDTO(Long sessionId,
                                 ZonedDateTime sessionStart,
                                 ZonedDateTime sessionEnd,
                                 Long elapsedTimeInSecond) {
}
