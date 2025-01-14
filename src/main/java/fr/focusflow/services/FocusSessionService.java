package fr.focusflow.services;

import fr.focusflow.dtos.FocusSessionDTO;
import fr.focusflow.dtos.SessionTimeInfoDTO;
import fr.focusflow.entities.FocusSession;
import fr.focusflow.exceptions.FocusSessionNotFoundException;
import fr.focusflow.exceptions.FocusSessionStatusException;

import java.util.List;

public interface FocusSessionService {


    FocusSessionDTO startOrResumeSession(Long taskId, Long sessionId) throws Exception;

    SessionTimeInfoDTO getSessionTimeInfo(Long sessionId) throws FocusSessionNotFoundException;

    FocusSession markFocusSessionAsCompleted(Long sessionId);

    List<FocusSessionDTO> getAllSessionByUser(Long userId);

    FocusSessionDTO markFocusSessionAsPending(Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException;

    FocusSessionDTO markFocusSessionAsDone(Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException;
}
