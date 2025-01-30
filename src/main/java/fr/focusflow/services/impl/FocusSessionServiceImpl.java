package fr.focusflow.services.impl;

import fr.focusflow.dtos.FocusSessionDTO;
import fr.focusflow.dtos.SessionTimeInfoDTO;
import fr.focusflow.entities.EStatus;
import fr.focusflow.entities.FocusSession;
import fr.focusflow.entities.Task;
import fr.focusflow.entities.User;
import fr.focusflow.exceptions.FocusSessionNotFoundException;
import fr.focusflow.exceptions.FocusSessionStatusException;
import fr.focusflow.exceptions.TaskNotFoundException;
import fr.focusflow.mappers.FocusSessionMapper;
import fr.focusflow.repositories.FocusSessionRepository;
import fr.focusflow.repositories.TaskRepository;
import fr.focusflow.services.AuthenticatedUserService;
import fr.focusflow.services.FocusSessionService;
import fr.focusflow.validations.FocusSessionStatusValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class FocusSessionServiceImpl implements FocusSessionService {

    private static final String THE_SESSION_IS_NOT_FOUND = "The session is not found !";

    private final FocusSessionRepository focusSessionRepository;
    private final TaskRepository taskRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final FocusSessionStatusValidator focusSessionStatusValidator;
    private final FocusSessionMapper focusSessionMapper;

    public FocusSessionServiceImpl(FocusSessionRepository focusSessionRepository,
                                   TaskRepository taskRepository,
                                   AuthenticatedUserService authenticatedUserService,
                                   FocusSessionStatusValidator focusSessionStatusValidator,
                                   FocusSessionMapper focusSessionMapper) {
        this.focusSessionRepository = focusSessionRepository;
        this.taskRepository = taskRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.focusSessionStatusValidator = focusSessionStatusValidator;
        this.focusSessionMapper = focusSessionMapper;

    }

    /**
     * Start new session if sessionId is null or resume existing session
     *
     * @param taskId
     * @param sessionId
     * @return a new FocusSessionDTO object
     * @throws TaskNotFoundException
     * @throws FocusSessionStatusException
     */
    @Transactional
    @Override
    public FocusSessionDTO startOrResumeSession(Long taskId, Long sessionId) throws TaskNotFoundException, FocusSessionStatusException {

        if (sessionId == null) {
            return startNewSession(taskId);
        }

        Optional<FocusSession> optionalExistingSession = focusSessionRepository.findById(sessionId);

        if (optionalExistingSession.isPresent()) {
            return resumeExistingSession(optionalExistingSession.get());
        } else {
            return startNewSession(taskId);
        }
    }

    /**
     * Start a new session by task ID
     *
     * @param taskId
     * @return a new FocusSessionDTO object
     * @throws TaskNotFoundException
     */
    private FocusSessionDTO startNewSession(Long taskId) throws TaskNotFoundException {
        FocusSession focusSessionResponse;

        User user = authenticatedUserService.getAuthenticatedUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found !"));

        FocusSession newFocusSession = FocusSession.builder()
                .user(user)
                .task(task)
                .status(EStatus.IN_PROGRESS)
                .build();

        focusSessionResponse = focusSessionRepository.save(newFocusSession);

        return focusSessionMapper.mapFocusSessionToFocusDTO(focusSessionResponse);
    }

    /**
     * Resumes an existing session that is in a pending state
     *
     * @param existingFocusSession
     * @return a FocusSessionDTO object
     * @throws FocusSessionStatusException
     */
    private FocusSessionDTO resumeExistingSession(FocusSession existingFocusSession) throws FocusSessionStatusException {

        if (EStatus.IN_PROGRESS.equals(existingFocusSession.getStatus())) {
            throw new FocusSessionStatusException("The Session already in progress");
        } else if (EStatus.DONE.equals(existingFocusSession.getStatus())) {
            throw new FocusSessionStatusException("Session already done");
        } else {

        }

        existingFocusSession.setStatus(EStatus.IN_PROGRESS);
        return focusSessionMapper.mapFocusSessionToFocusDTO(focusSessionRepository.save(existingFocusSession));
    }

    /**
     * Return session time info by session ID
     *
     * @param sessionId
     * @return SessionTimeInfoDTO object
     */
    @Override
    public SessionTimeInfoDTO getSessionTimeInfo(Long sessionId) throws FocusSessionNotFoundException {

        //get session
        FocusSession focusSession = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new FocusSessionNotFoundException(THE_SESSION_IS_NOT_FOUND));

        return SessionTimeInfoDTO
                .builder()
                .sessionId(focusSession.getId())
                .sessionStart(focusSession.getSessionStart())
                .sessionEnd(focusSession.getSessionEnd())
                .elapsedTimeInSecond(getElapsedSessionTimeInSecond(focusSession.getSessionStart()))
                .build();
    }

    /**
     * Marks session as completed
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
     * @return FocusSessionDTO List
     */
    @Override
    public List<FocusSessionDTO> getAllSessionByUser(Long userId) {
        return null;
    }

    /**
     * Marks session status as PENDING
     *
     * @param sessionId Session ID
     * @return FocusSessionDTO object after update
     */
    @Override
    public FocusSessionDTO markFocusSessionAsPending(Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException {

        FocusSession existingSession = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new FocusSessionNotFoundException(THE_SESSION_IS_NOT_FOUND));

        return updateCurrentSessionStatusWithNewStatus(existingSession, EStatus.PENDING);
    }


    /**
     * Mark session status as done
     *
     * @param sessionId
     * @return a FocusSessionDTO object
     */
    @Override
    public FocusSessionDTO markFocusSessionAsDone(Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException {
        FocusSession existingSession = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new FocusSessionNotFoundException(THE_SESSION_IS_NOT_FOUND));

        return updateCurrentSessionStatusWithNewStatus(existingSession, EStatus.DONE);
    }

    /**
     * Changes the status of existing session to the new status
     * Validates transition before saving
     *
     * @param existingSession The session to update
     * @param newStatus       The new session status to apply to the existing session
     * @return The updated FocusSession with the new status applied.
     * @throws FocusSessionStatusException
     */
    private FocusSessionDTO updateCurrentSessionStatusWithNewStatus(FocusSession existingSession, EStatus newStatus) throws FocusSessionStatusException {
        focusSessionStatusValidator.validateStatusTransition(newStatus, existingSession);
        existingSession.setStatus(newStatus);
        return focusSessionMapper.mapFocusSessionToFocusDTO(focusSessionRepository.save(existingSession));
    }

    /**
     * Get current elapsed session time in second
     *
     * @param sessionStart
     * @return a Long elapsed session time in second
     */
    public Long getElapsedSessionTimeInSecond(LocalDateTime sessionStart) {

        LocalDateTime now = LocalDateTime.now();

        if (sessionStart == null || now.isBefore(sessionStart)) {
            return Duration.ZERO.getSeconds();
        }

        /*
         * Cette methode  ne fonctionne pas a cause d'un nullPointerException
         * Pourtant les deux valeurs ne sont pas null
         * Duration elapsedTimeDuration = Duration.between(sessionStart, now);
         * Alternative avec now.toEpochSecond(ZoneOffset.UTC) - sessionStart.toEpochSecond(ZoneOffset.UTC);
         *
         */

        return now.toEpochSecond(ZoneOffset.UTC) - sessionStart.toEpochSecond(ZoneOffset.UTC);
    }
}
