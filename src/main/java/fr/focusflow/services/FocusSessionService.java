package fr.focusflow.services;

import fr.focusflow.dtos.SessionTimeInfoDTO;
import fr.focusflow.entities.FocusSession;
import fr.focusflow.exceptions.FocusSessionNotFoundException;
import fr.focusflow.exceptions.FocusSessionStatusException;

import java.util.List;

public interface FocusSessionService {


    FocusSession startOrResumeSession(Long taskId, Long sessionId) throws Exception;

    SessionTimeInfoDTO getSessionTimeInfo(Long sessionId) throws FocusSessionNotFoundException;

    FocusSession markFocusSessionAsCompleted(Long sessionId);

    List<FocusSession> getAllSessionByUser(Long userId);

    FocusSession markFocusSessionAsPending(Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException;

    FocusSession markFocusSessionAsDone(Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException;

    FocusSession markFocusSessionAsCancelled(Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException;
}
