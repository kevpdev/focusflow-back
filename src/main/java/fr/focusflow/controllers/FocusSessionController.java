package fr.focusflow.controllers;

import fr.focusflow.dtos.FocusSessionRequestDTO;
import fr.focusflow.entities.FocusSession;
import fr.focusflow.exceptions.FocusSessionNotFoundException;
import fr.focusflow.exceptions.FocusSessionStatusException;
import fr.focusflow.services.FocusSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/sessions")
public class FocusSessionController {

    private final FocusSessionService focusSessionService;

    public FocusSessionController(FocusSessionService focusSessionService) {
        this.focusSessionService = focusSessionService;
    }

    @Operation(summary = "Start or resume a session", description = "Starts a new session if no sessionId is provided, or resumes an existing session if a valid sessionId is provided.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session started or resumed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid session or task data provided"),
            @ApiResponse(responseCode = "404", description = "Task or session not found")
    })
    @PutMapping("/status/start")
    public ResponseEntity<FocusSession> startOrResumeSession(
            @RequestBody FocusSessionRequestDTO focusSessionRequestDTO) throws Exception {
        FocusSession focusSession = focusSessionService.startOrResumeSession(focusSessionRequestDTO.taskId(),
                focusSessionRequestDTO.sessionId());
        return ResponseEntity.status(HttpStatus.OK).body(focusSession);
    }

    @Operation(summary = "Mark session as pending", description = "Marks a session's status as pending.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session marked as pending successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition")
    })
    @PutMapping("/status/pending/{sessionId}")
    public ResponseEntity<FocusSession> markSessionStatusAsPending(
            @Parameter(description = "ID of the session to mark as pending") @PathVariable Long sessionId)
            throws FocusSessionNotFoundException, FocusSessionStatusException {
        FocusSession focusSession = focusSessionService.markFocusSessionAsPending(sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(focusSession);
    }

    @Operation(summary = "Mark session as done", description = "Marks a session's status as done.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session marked as done successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition")
    })
    @PutMapping("/status/done/{sessionId}")
    public ResponseEntity<FocusSession> markSessionStatusAsDone(
            @Parameter(description = "ID of the session to mark as done") @PathVariable Long sessionId)
            throws FocusSessionNotFoundException, FocusSessionStatusException {
        FocusSession focusSession = focusSessionService.markFocusSessionAsDone(sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(focusSession);
    }

    @Operation(summary = "Mark session as cancelled", description = "Cancels a session by marking its status as cancelled.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session marked as cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition")
    })
    @PutMapping("/status/cancelled/{sessionId}")
    public ResponseEntity<FocusSession> markSessionStatusAsCancelled(
            @Parameter(description = "ID of the session to mark as cancelled") @PathVariable Long sessionId)
            throws FocusSessionNotFoundException, FocusSessionStatusException {
        FocusSession focusSession = focusSessionService.markFocusSessionAsCancelled(sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(focusSession);
    }
}
