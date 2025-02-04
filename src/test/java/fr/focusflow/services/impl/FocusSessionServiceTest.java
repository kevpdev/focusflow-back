package fr.focusflow.services.impl;

import fr.focusflow.TestDataFactory;
import fr.focusflow.dtos.FocusSessionDTO;
import fr.focusflow.dtos.FocusSessionRequestDTO;
import fr.focusflow.dtos.SessionTimeInfoDTO;
import fr.focusflow.entities.FocusSession;
import fr.focusflow.entities.User;
import fr.focusflow.exceptions.FocusSessionNotFoundException;
import fr.focusflow.exceptions.FocusSessionRequestException;
import fr.focusflow.exceptions.TaskNotFoundException;
import fr.focusflow.mappers.FocusSessionMapper;
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
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FocusSessionServiceTest {

    static final Logger LOGGER = LoggerFactory.getLogger(FocusSessionServiceTest.class);

    @Mock
    FocusSessionRepository focusSessionRepository;

    @Mock
    TaskRepository taskRepository;

    @Mock
    AuthenticatedUserService authenticatedUserService;

    @Mock
    FocusSessionMapper focusSessionMapper;

    @InjectMocks
    FocusSessionServiceImpl focusSessionService;


    @Test
    void shouldThrowExceptionWhenDurationIsLessThan1ForCreateFocusSessionMethod() {
        //  Mocking data
        FocusSessionRequestDTO focusSessionRequestDTO = FocusSessionRequestDTO.create(1L, 0L);

        assertThrows(FocusSessionRequestException.class, () -> focusSessionService.createFocusSession(focusSessionRequestDTO));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNullForCreateFocusSessionMethod() {
        //  Mocking data
        FocusSessionRequestDTO focusSessionRequestDTO = FocusSessionRequestDTO.create(1L, null);

        assertThrows(FocusSessionRequestException.class, () -> focusSessionService.createFocusSession(focusSessionRequestDTO));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsGreaterThan90ForCreateFocusSessionMethod() {
        //  Mocking data
        FocusSessionRequestDTO focusSessionRequestDTO = FocusSessionRequestDTO.create(1L, 120L);

        assertThrows(FocusSessionRequestException.class, () -> focusSessionService.createFocusSession(focusSessionRequestDTO));
    }

    @Test
    void shouldThrowExceptionWhenTaskIdIsNullForCreateFocusSessionMethod() {
        //  Mocking data
        FocusSessionRequestDTO focusSessionRequestDTO = FocusSessionRequestDTO.create(null, 30L);

        assertThrows(FocusSessionRequestException.class, () -> focusSessionService.createFocusSession(focusSessionRequestDTO));
    }

    @Test
    void shouldCreateFocusSessionWhenActiveSessionNotFound() throws TaskNotFoundException, FocusSessionRequestException {

        //  Mocking data
        FocusSessionRequestDTO focusSessionRequestDTO = FocusSessionRequestDTO.create(1L, 30L);
        FocusSessionDTO focusSessionDTO = (TestDataFactory.createFocusSessionDTO(1L, 30L));
        FocusSession focusSessionSaveResult = TestDataFactory.createFocusSession(focusSessionDTO);
        User mockUser = TestDataFactory.createUser();


        //Mocking methods calls in getActiveSessionByUserId method service
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(mockUser);
        when(focusSessionRepository.findSessionActiveByUserId(2L)).thenReturn(Optional.empty());

        //Mocking methods calls in startNewSession method service
        when(taskRepository.findById(focusSessionRequestDTO.taskId())).thenReturn(Optional.ofNullable(TestDataFactory.createDefaultTask(1L, mockUser)));
        when(focusSessionRepository.save(any(FocusSession.class))).thenReturn(focusSessionSaveResult);
        when(focusSessionMapper.mapFocusSessionToFocusDTO(any(FocusSession.class))).thenReturn(focusSessionDTO);


        FocusSessionDTO result = focusSessionService.createFocusSession(focusSessionRequestDTO);

        Assertions.assertEquals(focusSessionDTO, result);

        // Verifying that the methods were called
        verify(authenticatedUserService).getAuthenticatedUser();
        verify(focusSessionRepository).findSessionActiveByUserId(2L);
        verify(taskRepository).findById(1L);
        verify(focusSessionRepository).save(any(FocusSession.class));
        verify(focusSessionMapper).mapFocusSessionToFocusDTO(any(FocusSession.class));

    }

    @Test
    void shouldCreateFocusSessionWhenActiveSessionIsDone() throws TaskNotFoundException, FocusSessionRequestException {

        //  Mocking data
        FocusSessionRequestDTO focusSessionRequestDTO = FocusSessionRequestDTO.create(1L, 30L);
        FocusSessionDTO focusSessionDTO = (TestDataFactory.createFocusSessionDTO(1L, 30L));
        FocusSession focusSessionSaveResult = TestDataFactory.createFocusSession(focusSessionDTO);
        User mockUser = TestDataFactory.createUser();


        //Mocking methods calls in getActiveSessionByUserId method service
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(mockUser);
        when(focusSessionRepository.findSessionActiveByUserId(2L)).thenReturn(Optional.ofNullable(TestDataFactory.createFocusSession(focusSessionDTO, mockUser)));

        //Mocking methods calls in startNewSession method service
        when(taskRepository.findById(focusSessionRequestDTO.taskId())).thenReturn(Optional.ofNullable(TestDataFactory.createDefaultTask(1L, mockUser)));
        when(focusSessionRepository.save(any(FocusSession.class))).thenReturn(focusSessionSaveResult);
        when(focusSessionMapper.mapFocusSessionToFocusDTO(any(FocusSession.class))).thenReturn(focusSessionDTO);


        FocusSessionDTO result = focusSessionService.createFocusSession(focusSessionRequestDTO);

        Assertions.assertEquals(focusSessionDTO, result);

        // Verifying that the methods were called
        verify(authenticatedUserService).getAuthenticatedUser();
        verify(focusSessionRepository).findSessionActiveByUserId(2L);
        verify(taskRepository).findById(1L);
        verify(focusSessionRepository, times(2)).save(any(FocusSession.class));
        verify(focusSessionMapper).mapFocusSessionToFocusDTO(any(FocusSession.class));

    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        //  Mocking data
        FocusSessionRequestDTO focusSessionRequestDTO = FocusSessionRequestDTO.create(1L, 30L);
        User mockUser = TestDataFactory.createUser();
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(mockUser);
        when(focusSessionRepository.findSessionActiveByUserId(2L)).thenReturn(Optional.empty());
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When

        assertThrows(TaskNotFoundException.class, () -> focusSessionService.createFocusSession(focusSessionRequestDTO));

        // Verifying that the methods were called
        verify(authenticatedUserService).getAuthenticatedUser();
        verify(focusSessionRepository).findSessionActiveByUserId(2L);
        verify(taskRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        //  Mocking data
        FocusSessionRequestDTO focusSessionRequestDTO = FocusSessionRequestDTO.create(1L, 30L);

        //when authentication user throw exception
        when(authenticatedUserService.getAuthenticatedUser()).thenThrow(new UsernameNotFoundException("User not found"));
        assertThrows(UsernameNotFoundException.class, () -> focusSessionService.createFocusSession(focusSessionRequestDTO));

        // Verifying that the methods were called
        verify(authenticatedUserService).getAuthenticatedUser();

    }

    @Test
    void shouldThrowDataAccessExceptionWhenFocusSessionRepositorySaveThrowException() {
        //  Mocking data
        FocusSessionRequestDTO focusSessionRequestDTO = FocusSessionRequestDTO.create(1L, 30L);
        User mockUser = TestDataFactory.createUser();

        //Mocking methods calls in getActiveSessionByUserId method service
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(mockUser);
        when(focusSessionRepository.findSessionActiveByUserId(2L)).thenReturn(Optional.empty());

        //Mocking methods calls in startNewSession method service
        when(taskRepository.findById(focusSessionRequestDTO.taskId())).thenReturn(Optional.ofNullable(TestDataFactory.createDefaultTask(1L, mockUser)));
        when(focusSessionRepository.save(any(FocusSession.class))).thenThrow(new DataAccessException("...") {
        });

        assertThrows(DataAccessException.class, () -> focusSessionService.createFocusSession(focusSessionRequestDTO));

        // Verifying that the methods were called
        verify(authenticatedUserService).getAuthenticatedUser();
        verify(focusSessionRepository).findSessionActiveByUserId(2L);
        verify(taskRepository).findById(1L);
        verify(focusSessionRepository).save(any(FocusSession.class));

    }

    @Test
    void shouldReturnElapsedSessionTimeInSecond() {
        ZonedDateTime tInstant = ZonedDateTime.of(
                2025, 10, 22, 19, 30, 0, 0, ZoneId.of("Europe/Paris")
        );

        ZonedDateTime start = ZonedDateTime.of(
                2025, 10, 22, 19, 0, 0, 0, ZoneId.of("Europe/Paris")
        );

        // Mocking ZonedDateTime.now() to return the fixed time 'tInstant'
        try (MockedStatic<ZonedDateTime> mockedStatic = mockStatic(ZonedDateTime.class)) {
            mockedStatic.when(ZonedDateTime::now).thenReturn(tInstant);

            System.out.println("now (mocked): " + ZonedDateTime.now());
            System.out.println("T instant: " + tInstant);
            System.out.println("session START : " + start);

            Assertions.assertEquals(tInstant, ZonedDateTime.now());

            Long result = focusSessionService.getElapsedSessionTimeInSecond(start);

            // Vérifie le temps écoulé (30 minutes = 1800 secondes)
            Assertions.assertEquals(1800L, result);
        }
    }

    @Test
    void shouldReturnSessionInfoIfSessionExist() throws FocusSessionNotFoundException {

        ZonedDateTime tInstant = ZonedDateTime.of(
                2025, 10, 22, 19, 30, 0, 0, ZoneId.of("Europe/Paris")
        );

        ZonedDateTime start = ZonedDateTime.of(
                2025, 10, 22, 19, 0, 0, 0, ZoneId.of("Europe/Paris")
        );


        try (MockedStatic<ZonedDateTime> mockedStatic = mockStatic(ZonedDateTime.class)) {

            //mock session
            mockedStatic.when(ZonedDateTime::now).thenReturn(tInstant);

            FocusSession existingSession = TestDataFactory.createFocusSession(TestDataFactory.createFocusSessionDTO(1L, 60L));
            existingSession.setSessionStart(start);
            existingSession.setCreatedAt(start);

            // mock find session by id
            when(focusSessionRepository.findById(anyLong())).thenReturn(Optional.of(existingSession));

            SessionTimeInfoDTO sessionTimeInfoDTO = focusSessionService.getSessionTimeInfo(1L);

            System.out.println("now (mocked): " + ZonedDateTime.now());
            System.out.println("T instant: " + tInstant);
            System.out.println("session START : " + sessionTimeInfoDTO.sessionStart());


            Assertions.assertEquals(existingSession.getId(), sessionTimeInfoDTO.sessionId());
            Assertions.assertEquals(existingSession.getSessionStart(), sessionTimeInfoDTO.sessionStart());
            Assertions.assertEquals(1800L, sessionTimeInfoDTO.elapsedTimeInSecond());

            verify(focusSessionRepository).findById(anyLong());
        }
    }
}
