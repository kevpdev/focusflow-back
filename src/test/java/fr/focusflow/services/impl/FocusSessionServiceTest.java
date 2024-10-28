package fr.focusflow.services.impl;

import fr.focusflow.TestDataFactory;
import fr.focusflow.dtos.SessionTimeInfoDTO;
import fr.focusflow.entities.EFocusSessionStatus;
import fr.focusflow.entities.FocusSession;
import fr.focusflow.entities.Task;
import fr.focusflow.entities.User;
import fr.focusflow.exceptions.FocusSessionNotFoundException;
import fr.focusflow.exceptions.FocusSessionStatusException;
import fr.focusflow.exceptions.TaskNotFoundException;
import fr.focusflow.repositories.FocusSessionRepository;
import fr.focusflow.repositories.TaskRepository;
import fr.focusflow.services.AuthenticatedUserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FocusSessionServiceTest {

    static final Logger LOGGER = LoggerFactory.getLogger(FocusSessionServiceTest.class);

    @Mock
    FocusSessionRepository focusSessionRepository;

    @Mock
    TaskRepository taskRepository;

    @Mock
    AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    FocusSessionServiceImpl focusSessionService;

    @Test
    public void ShouldCreateSessionWhenSessionIdIsNull() throws Exception {

        Long sessionId = null;
        Long taskId = 1L;

        User user = TestDataFactory.createUser();
        Task task = TestDataFactory.createTask(taskId, user);

        // Mock taskRepository
        when(taskRepository.findById(taskId)).thenReturn(Optional.ofNullable(task));

        // Mock authenticatedUserService
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        FocusSession focusSessionSaveResult = TestDataFactory.createFocusSession(1L);

        // Mock focusSessionRepository save
        when(focusSessionRepository.save(any(FocusSession.class))).thenReturn(focusSessionSaveResult);

        // Call real focusSessionService
        FocusSession focusSessionResponse = focusSessionService.startOrResumeSession(taskId, sessionId);

        Assertions.assertNotNull(focusSessionResponse);
        Assertions.assertEquals(EFocusSessionStatus.IN_PROGRESS, focusSessionResponse.getStatus());
        Assertions.assertEquals(user, focusSessionResponse.getUser());
        Assertions.assertEquals(task, focusSessionResponse.getTask());

        // verify that focusSessionRepository.findById was never called if session ID is null
        verify(focusSessionRepository, never()).findById(any());

        // verify that focusSessionRepository.save was called
        verify(focusSessionRepository).save(any(FocusSession.class));

    }

    @Test
    public void ShouldThrowExceptionIFSessionStatusIsNotPending() throws FocusSessionStatusException, TaskNotFoundException {

        Long sessionId = 1L;
        Long taskId = 1L;

        FocusSession focusSessionSaveResult = TestDataFactory.createFocusSession(sessionId);

        // Mock focusSessionRepository save
        when(focusSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(focusSessionSaveResult));

        Assertions.assertThrowsExactly(FocusSessionStatusException.class, () -> {
            focusSessionService.startOrResumeSession(taskId, sessionId);
        });

        // verify that focusSessionRepository.save was never called if session ID is null
        verify(focusSessionRepository, never()).save(any(FocusSession.class));

        // verify that focusSessionRepository.findbyId was called
        verify(focusSessionRepository).findById(sessionId);

    }

    @Test
    public void ShouldResumeExistingSessionIfStatusIsPending() throws FocusSessionStatusException, TaskNotFoundException {

        Long sessionId = 1L;
        Long taskId = 1L;

        FocusSession existingFocusSession = TestDataFactory.createFocusSession(sessionId);
        existingFocusSession.setStatus(EFocusSessionStatus.PENDING);

        FocusSession focusSessionSaveResult = TestDataFactory.createFocusSession(sessionId);

        // Mock focusSessionRepository findById
        when(focusSessionRepository.findById(sessionId)).thenReturn(Optional.of(existingFocusSession));

        // Mock focusSessionRepository save
        when(focusSessionRepository.save(any(FocusSession.class))).thenReturn(focusSessionSaveResult);

        // Call real focusSessionService
        FocusSession focusSessionResponse = focusSessionService.startOrResumeSession(taskId, sessionId);

        Assertions.assertNotNull(focusSessionResponse);
        Assertions.assertEquals(EFocusSessionStatus.IN_PROGRESS, focusSessionResponse.getStatus());
        Assertions.assertEquals(2L, focusSessionResponse.getUser().getId());
        Assertions.assertEquals(1L, focusSessionResponse.getTask().getId());


        // verify that focusSessionRepository.findbyId was called
        verify(focusSessionRepository).findById(sessionId);


        // verify that focusSessionRepository.save was called
        verify(focusSessionRepository).save(any(FocusSession.class));

    }

    @Test
    public void shouldReturnElapsedSessionTimeInSecond() {
        LocalDateTime start = LocalDateTime.of(2024, 10, 22, 18, 30, 0);
        LocalDateTime tInstant = LocalDateTime.of(2024, 10, 22, 19, 0, 0);

        // Mocking LocalDateTime.now() to return the fixed time 'tInstant'
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(tInstant);

            System.out.println("now (mocked): " + LocalDateTime.now());
            System.out.println("T instant: " + tInstant);
            System.out.println("session START : " + start);

            Assertions.assertEquals(tInstant, LocalDateTime.now());

            Long result = focusSessionService.getElapsedSessionTimeInSecond(start);

            // Vérifie le temps écoulé (30 minutes = 1800 secondes)
            Assertions.assertEquals(1800L, result);
        }
    }

    @Test
    public void shouldReturnSessionInfoIfSessionExist() throws FocusSessionNotFoundException {

        LocalDateTime tInstant = LocalDateTime.of(2024, 10, 22, 19, 0, 0);
        LocalDateTime start = LocalDateTime.of(2024, 10, 22, 18, 30, 0);

        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {

            //mock session
            mockedStatic.when(LocalDateTime::now).thenReturn(tInstant);

            FocusSession existingSession = TestDataFactory.createFocusSession(1L);
            existingSession.setSessionStart(start);
            existingSession.setCreatedAt(start);

            // mock find session by id
            when(focusSessionRepository.findById(anyLong())).thenReturn(Optional.of(existingSession));

            SessionTimeInfoDTO sessionTimeInfoDTO = focusSessionService.getSessionTimeInfo(1L);

            System.out.println("now (mocked): " + LocalDateTime.now());
            System.out.println("T instant: " + tInstant);
            System.out.println("session START : " + sessionTimeInfoDTO.sessionStart());


            Assertions.assertEquals(existingSession.getId(), sessionTimeInfoDTO.sessionId());
            Assertions.assertEquals(existingSession.getSessionStart(), sessionTimeInfoDTO.sessionStart());
            Assertions.assertEquals(1800L, sessionTimeInfoDTO.elapsedTimeInSecond());

            verify(focusSessionRepository).findById(anyLong());
        }
    }
}
