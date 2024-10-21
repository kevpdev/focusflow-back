package fr.focusflow.controllers;

import fr.focusflow.dtos.FocusSessionRequestDTO;
import fr.focusflow.entities.FocusSession;
import fr.focusflow.services.FocusSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sessions")
public class FocusSessionController {

    private final FocusSessionService focusSessionService;

    public FocusSessionController(FocusSessionService focusSessionService) {
        this.focusSessionService = focusSessionService;
    }

    @PutMapping("/start")
    public ResponseEntity<FocusSession> startOrResumeSession(@RequestBody FocusSessionRequestDTO focusSessionRequestDTO) throws Exception {
        FocusSession focusSession = focusSessionService.startOrResumeSession(focusSessionRequestDTO.taskId(),
                focusSessionRequestDTO.sessionId());
        return ResponseEntity.status(HttpStatus.OK).body(focusSession);
    }
}
