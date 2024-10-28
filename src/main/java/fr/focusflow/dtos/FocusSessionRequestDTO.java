package fr.focusflow.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;


public record FocusSessionRequestDTO(@JsonProperty("taskId") Long taskId, @JsonProperty("sessionId") Long sessionId) {
}
