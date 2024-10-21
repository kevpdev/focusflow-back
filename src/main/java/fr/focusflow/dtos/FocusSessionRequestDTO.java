package fr.focusflow.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FocusSessionRequestDTO(@JsonProperty("taskId") Long taskId, @JsonProperty("sessionId") Long sessionId) {
}
