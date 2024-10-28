package fr.focusflow.controllers;

import fr.focusflow.dtos.SessionTimeInfoDTO;
import fr.focusflow.exceptions.FocusSessionNotFoundException;
import fr.focusflow.services.FocusSessionService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketFocusSessionController {

    private final FocusSessionService sessionService;

    public WebSocketFocusSessionController(FocusSessionService focusSessionService) {
        this.sessionService = focusSessionService;
    }

    @MessageMapping("/sessions/{sessionId}/info")
    @SendTo("/topic/sessions/{sessionId}/info")
    public SessionTimeInfoDTO getSessionInfo(@DestinationVariable Long sessionId) throws FocusSessionNotFoundException {
        return sessionService.getSessionTimeInfo(sessionId);
    }
}
