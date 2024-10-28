package fr.focusflow.controllers;

import fr.focusflow.dtos.FocusSessionRequestDTO;
import fr.focusflow.entities.FocusSession;
import fr.focusflow.exceptions.FocusSessionNotFoundException;
import fr.focusflow.exceptions.FocusSessionStatusException;
import fr.focusflow.services.FocusSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
public class FocusSessionController {

    private final FocusSessionService focusSessionService;

    public FocusSessionController(FocusSessionService focusSessionService) {
        this.focusSessionService = focusSessionService;
    }

    @PutMapping("/status/start")
    public ResponseEntity<FocusSession> startOrResumeSession(@RequestBody FocusSessionRequestDTO focusSessionRequestDTO) throws Exception {
        FocusSession focusSession = focusSessionService.startOrResumeSession(focusSessionRequestDTO.taskId(),
                focusSessionRequestDTO.sessionId());
        return ResponseEntity.status(HttpStatus.OK).body(focusSession);
    }

    @PutMapping("/status/pending/{sessionId}")
    public ResponseEntity<FocusSession> markSessionStatusAsPending(@PathVariable Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException {
        FocusSession focusSession = focusSessionService.markFocusSessionAsPending(sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(focusSession);
    }

    @PutMapping("/status/done/{sessionId}")
    public ResponseEntity<FocusSession> markSessionStatusAsDone(@PathVariable Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException {
        FocusSession focusSession = focusSessionService.markFocusSessionAsDone(sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(focusSession);
    }

    @PutMapping("/status/cancelled/{sessionId}")
    public ResponseEntity<FocusSession> markSessionStatusAsCancelled(@PathVariable Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException {
        FocusSession focusSession = focusSessionService.markFocusSessionAsCancelled(sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(focusSession);
    }
}
