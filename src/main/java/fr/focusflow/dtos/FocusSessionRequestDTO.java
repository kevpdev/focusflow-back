package fr.focusflow.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;


public record FocusSessionRequestDTO(@JsonProperty("taskId") Long taskId,
                                     Long durationInMinutes) {
    @Builder
    public static FocusSessionRequestDTO create(Long taskId, Long durationInMinutes) {
        return new FocusSessionRequestDTO(taskId, durationInMinutes);
    }
}
