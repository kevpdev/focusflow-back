package fr.focusflow.services.impl;

import fr.focusflow.dtos.SessionTimeInfoDTO;
import fr.focusflow.entities.EFocusSessionStatus;
import fr.focusflow.entities.FocusSession;
import fr.focusflow.entities.Task;
import fr.focusflow.entities.User;
import fr.focusflow.exceptions.FocusSessionStatusException;
import fr.focusflow.exceptions.TaskNotFoundException;
import fr.focusflow.repositories.FocusSessionRepository;
import fr.focusflow.repositories.TaskRepository;
import fr.focusflow.services.AuthenticatedUserService;
import fr.focusflow.services.FocusSessionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FocusSessionServiceImpl implements FocusSessionService {

    private final FocusSessionRepository focusSessionRepository;
    private final TaskRepository taskRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public FocusSessionServiceImpl(FocusSessionRepository focusSessionRepository,
                                   TaskRepository taskRepository,
                                   AuthenticatedUserService authenticatedUserService) {
        this.focusSessionRepository = focusSessionRepository;
        this.taskRepository = taskRepository;
        this.authenticatedUserService = authenticatedUserService;

    }

    /**
     * Start new session if sessionId is null or resume existing session
     *
     * @param taskId
     * @param sessionId
     * @return FocusSessionObject
     * @throws TaskNotFoundException
     * @throws FocusSessionStatusException
     */
    @Override
    public FocusSession startOrResumeSession(Long taskId, Long sessionId) throws TaskNotFoundException, FocusSessionStatusException {

        Optional<FocusSession> optionalExistingSession = focusSessionRepository.findById(sessionId);

        if (optionalExistingSession.isPresent()) {
            return resumeExistingSession(optionalExistingSession.get());
        } else {
            return startNewSession(taskId);
        }
    }

    private FocusSession startNewSession(Long taskId) throws TaskNotFoundException {
        FocusSession focusSessionResponse;

        User user = authenticatedUserService.getAuthenticatedUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found !"));

        FocusSession newFocusSession = FocusSession.builder()
                .user(user)
                .task(task)
                .status(EFocusSessionStatus.IN_PROGRESS)
                .build();

        focusSessionResponse = focusSessionRepository.save(newFocusSession);

        return focusSessionResponse;
    }

    private FocusSession resumeExistingSession(FocusSession existingFocusSession) throws FocusSessionStatusException {

        if (EFocusSessionStatus.IN_PROGRESS.equals(existingFocusSession.getStatus())) {
            throw new FocusSessionStatusException("Session already in progress");
        } else if (EFocusSessionStatus.DONE.equals(existingFocusSession.getStatus())
                || EFocusSessionStatus.CANCELLED.equals(existingFocusSession.getStatus())) {
            throw new FocusSessionStatusException("Session already done or canceled");
        } else {
            existingFocusSession.setStatus(EFocusSessionStatus.IN_PROGRESS);
            return focusSessionRepository.save(existingFocusSession);
        }
    }

    /**
     * Return session time info by session ID
     *
     * @param sessionId
     * @return SessionTimeInfoDTO object
     */
    @Override
    public SessionTimeInfoDTO getSessionTimeInfo(Long sessionId) {
        return null;
    }

    /**
     * Mark session as completed
     *
     * @param sessionId
     * @return FocusSession object
     */
    @Override
    public FocusSession markFocusSessionAsCompleted(Long sessionId) {
        return null;
    }

    /**
     * Get all sessions by user ID
     *
     * @param userId
     * @return FocusSession List
     */
    @Override
    public List<FocusSession> getAllSessionByUser(Long userId) {
        return null;
    }
}
