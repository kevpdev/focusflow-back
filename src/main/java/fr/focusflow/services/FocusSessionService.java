package fr.focusflow.services;

import fr.focusflow.dtos.SessionTimeInfoDTO;
import fr.focusflow.entities.FocusSession;

import java.util.List;

public interface FocusSessionService {

    FocusSession startOrResumeSession(Long taskId, Long sessionId) throws Exception;

    SessionTimeInfoDTO getSessionTimeInfo(Long sessionId);

    FocusSession markFocusSessionAsCompleted(Long sessionId);

    List<FocusSession> getAllSessionByUser(Long userId);

}
