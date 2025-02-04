package fr.focusflow.controllers;

import fr.focusflow.dtos.FocusSessionDTO;
import fr.focusflow.dtos.FocusSessionRequestDTO;
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

    @Operation(summary = "Start  a new session", description = "Starts a new session and deletes a potentially active session .")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session started or resumed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid session or task data provided"),
            @ApiResponse(responseCode = "404", description = "Task or session not found")
    })
    @PutMapping("/status/start")
    public ResponseEntity<FocusSessionDTO> startOrResumeSession(
            @RequestBody FocusSessionRequestDTO focusSessionRequestDTO) throws Exception {
        FocusSessionDTO focusSession = focusSessionService.createFocusSession(focusSessionRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(focusSession);
    }

    @Operation(summary = "Mark session as pending", description = "Marks a session's status as pending.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session marked as pending successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition")
    })
    @PutMapping("/status/pending/{sessionId}")
    public ResponseEntity<FocusSessionDTO> markSessionStatusAsPending(
            @Parameter(description = "ID of the session to mark as pending") @PathVariable Long sessionId)
            throws FocusSessionNotFoundException, FocusSessionStatusException {
        FocusSessionDTO focusSession = focusSessionService.markFocusSessionAsPending(sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(focusSession);
    }

    @Operation(summary = "Mark session as in progress", description = "Marks a session's status as in progress.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session marked as in progress successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition")
    })
    @PutMapping("/status/resume/{sessionId}")
    public ResponseEntity<FocusSessionDTO> markSessionStatusAsInProgress(
            @Parameter(description = "ID of the session to mark as in progress") @PathVariable Long sessionId)
            throws FocusSessionNotFoundException, FocusSessionStatusException {
        FocusSessionDTO focusSession = focusSessionService.markFocusSessionAsInProgress(sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(focusSession);
    }

    @Operation(summary = "Mark session as done", description = "Marks a session's status as done.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session marked as done successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition")
    })
    @PutMapping("/status/done/{sessionId}")
    public ResponseEntity<FocusSessionDTO> markSessionStatusAsDone(
            @Parameter(description = "ID of the session to mark as done") @PathVariable Long sessionId)
            throws FocusSessionNotFoundException, FocusSessionStatusException {
        FocusSessionDTO focusSession = focusSessionService.markFocusSessionAsDone(sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(focusSession);
    }

}
