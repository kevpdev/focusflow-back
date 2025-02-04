package fr.focusflow.services.impl;

import fr.focusflow.dtos.FocusSessionDTO;
import fr.focusflow.dtos.FocusSessionRequestDTO;
import fr.focusflow.dtos.SessionTimeInfoDTO;
import fr.focusflow.entities.EStatus;
import fr.focusflow.entities.FocusSession;
import fr.focusflow.entities.Task;
import fr.focusflow.entities.User;
import fr.focusflow.exceptions.FocusSessionNotFoundException;
import fr.focusflow.exceptions.FocusSessionRequestException;
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
import java.time.ZonedDateTime;
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
     * Start new session and delete active session if already exist
     *
     * @param focusSessionRequestDTO
     * @return new FocusSessionDTO object
     * @throws TaskNotFoundException
     */
    @Transactional
    @Override
    public FocusSessionDTO createFocusSession(FocusSessionRequestDTO focusSessionRequestDTO) throws TaskNotFoundException, FocusSessionRequestException {


        // durationInMinutes must be greater than 0
        if (focusSessionRequestDTO.durationInMinutes() == null || focusSessionRequestDTO.durationInMinutes() <= 0 || focusSessionRequestDTO.durationInMinutes() > 90) {
            throw new FocusSessionRequestException("Duration must be not null and greater than 0 and less than 90 minutes");
        }

        // taskId must be not null
        if (focusSessionRequestDTO.taskId() == null) {
            throw new FocusSessionRequestException("Task id must be not null");
        }

        User user = authenticatedUserService.getAuthenticatedUser();

        // Check if existing session
        Optional<FocusSession> optionalExistingSession = getActiveSessionByUserId(user.getId());

        // stop existing session if exist
        optionalExistingSession.ifPresent(existingSession -> {
            existingSession.setStatus(EStatus.DONE);
            existingSession.setSessionEnd(ZonedDateTime.now());
            focusSessionRepository.save(existingSession);
        });

        return startNewSession(focusSessionRequestDTO.taskId(), focusSessionRequestDTO.durationInMinutes(), user);
    }


    /**
     * Get active Session by user ID (Status != DONE)
     *
     * @param userId
     * @return an optional FocusSession object
     */
    private Optional<FocusSession> getActiveSessionByUserId(Long userId) {
        return focusSessionRepository.findSessionActiveByUserId(userId);
    }


    /**
     * @param taskId            task id
     * @param durationInMinutes durationInMinutes in minutes
     * @param user              user
     * @return a new FocusSessionDTO object
     * @throws TaskNotFoundException
     */
    private FocusSessionDTO startNewSession(Long taskId, Long durationInMinutes, User user) throws TaskNotFoundException {
        FocusSession focusSessionResponse;

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found !"));

        FocusSession newFocusSession = FocusSession.builder()
                .user(user)
                .task(task)
                .sessionEnd(ZonedDateTime.now().plusMinutes(durationInMinutes))
                .status(EStatus.IN_PROGRESS)
                .build();

        focusSessionResponse = focusSessionRepository.save(newFocusSession);

        return focusSessionMapper.mapFocusSessionToFocusDTO(focusSessionResponse);
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
     * Marks session status as PENDING
     *
     * @param sessionId Session ID
     * @return FocusSessionDTO object after update
     */
    @Override
    public FocusSessionDTO markFocusSessionAsPending(Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException {

        FocusSession existingSession = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new FocusSessionNotFoundException(THE_SESSION_IS_NOT_FOUND));

        return updateCurrentSessionWithNewStatus(existingSession, EStatus.PENDING);
    }

    /**
     * Marks session status as IN_PROGRESS
     *
     * @param sessionId Session ID
     * @return FocusSessionDTO object after update
     */
    @Override
    public FocusSessionDTO markFocusSessionAsInProgress(Long sessionId) throws FocusSessionNotFoundException, FocusSessionStatusException {

        FocusSession existingSession = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new FocusSessionNotFoundException(THE_SESSION_IS_NOT_FOUND));

        return updateCurrentSessionWithNewStatus(existingSession, EStatus.IN_PROGRESS);
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

        return updateCurrentSessionWithNewStatus(existingSession, EStatus.DONE);
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
    private FocusSessionDTO updateCurrentSessionWithNewStatus(FocusSession existingSession, EStatus newStatus) throws FocusSessionStatusException {
        focusSessionStatusValidator.validateStatusTransition(newStatus, existingSession);
        existingSession.setStatus(newStatus);
        if (newStatus.equals(EStatus.DONE)) {
            existingSession.setSessionEnd(ZonedDateTime.now());
        }
        return focusSessionMapper.mapFocusSessionToFocusDTO(focusSessionRepository.save(existingSession));
    }

    /**
     * Get current elapsed session time in second
     *
     * @param sessionStart
     * @return a Long elapsed session time in second
     */
    public Long getElapsedSessionTimeInSecond(ZonedDateTime sessionStart) {

        ZonedDateTime now = ZonedDateTime.now();

        if (sessionStart == null || now.isBefore(sessionStart)) {
            return Duration.ZERO.getSeconds();
        }

        return now.toEpochSecond() - sessionStart.toEpochSecond();
    }
}
