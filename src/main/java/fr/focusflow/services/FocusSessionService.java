package fr.focusflow.services;

import fr.focusflow.dtos.FocusSessionDTO;
import fr.focusflow.dtos.FocusSessionRequestDTO;
import fr.focusflow.dtos.SessionTimeInfoDTO;
import fr.focusflow.exceptions.FocusSessionNotFoundException;
import fr.focusflow.exceptions.FocusSessionStatusException;

public interface FocusSessionService {


    FocusSessionDTO createFocusSession(FocusSessionRequestDTO focusSessionRequestDTO) throws Exception;

    SessionTimeInfoDTO getSessionTimeInfo(Long sessionId) throws FocusSessionNotFoundException;

    FocusSessionDTO markFocusSessionAsPending(Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException;

    FocusSessionDTO markFocusSessionAsInProgress(Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException;

    FocusSessionDTO markFocusSessionAsDone(Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException;

}
