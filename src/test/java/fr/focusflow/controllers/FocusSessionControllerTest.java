package fr.focusflow.controllers;

import fr.focusflow.TestDataFactory;
import fr.focusflow.TestUtil;
import fr.focusflow.dtos.FocusSessionDTO;
import fr.focusflow.dtos.FocusSessionRequestDTO;
import fr.focusflow.entities.EStatus;
import fr.focusflow.security.CustomUserDetailsService;
import fr.focusflow.security.JwtTokenProvider;
import fr.focusflow.security.SecurityConfig;
import fr.focusflow.services.FocusSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(FocusSessionController.class)
class FocusSessionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    FocusSessionService focusSessionService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private String authorizationHeader;

    @BeforeEach
    void setUp() {
        authorizationHeader = "Bearer eykds5fsdg55sf5sdf5sf5sdf5sf_fake_token";

        // initialisatio context spring pour le bean authentication
        TestDataFactory.setUpSecurityContext();
    }


    @Test
    void shouldStartSession() throws Exception {
        FocusSessionDTO focusSessionDTO = TestDataFactory.createFocusSessionDTO(1L, 30L);

        //Request
        FocusSessionRequestDTO focusSessionRequestDTO = FocusSessionRequestDTO.create(1L, 30L);
        String jsonContent = TestUtil.objectToJsonMapper(focusSessionRequestDTO);


        // mocked service
        when(focusSessionService.createFocusSession(focusSessionRequestDTO)).thenReturn(focusSessionDTO);

        mockMvc.perform(put("/api/v1/sessions/status/start")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(focusSessionDTO.id()))
                .andExpect(jsonPath("$.taskId").value(focusSessionDTO.taskId()))
                .andExpect(jsonPath("$.userId").value(focusSessionDTO.userId()))
                .andExpect(jsonPath("$.sessionStart").isNotEmpty())
                .andExpect(jsonPath("$.sessionEnd").isNotEmpty())
                .andExpect(jsonPath("$.status").value(EStatus.IN_PROGRESS.name()));

        verify(focusSessionService).createFocusSession(focusSessionRequestDTO);
    }

    @Test
    void shouldMarkSessionAsPending() throws Exception {

        Long sessionId = 1L;
        FocusSessionDTO focusSessionDTO = TestDataFactory.createFocusSessionDTO(sessionId, EStatus.PENDING, 30L);

        when(focusSessionService.markFocusSessionAsPending(sessionId)).thenReturn(focusSessionDTO);

        mockMvc.perform(put("/api/v1/sessions/status/pending/{sessionId}", sessionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId))
                .andExpect(jsonPath("$.status").value(EStatus.PENDING.name()));

        verify(focusSessionService).markFocusSessionAsPending(sessionId);
    }

    @Test
    void shouldMarkSessionAsInProgress() throws Exception {

        Long sessionId = 1L;
        FocusSessionDTO focusSessionDTO = TestDataFactory.createFocusSessionDTO(sessionId, 30L);

        when(focusSessionService.markFocusSessionAsInProgress(sessionId)).thenReturn(focusSessionDTO);

        mockMvc.perform(put("/api/v1/sessions/status/resume/{sessionId}", sessionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId))
                .andExpect(jsonPath("$.status").value(EStatus.IN_PROGRESS.name()));

        verify(focusSessionService).markFocusSessionAsInProgress(sessionId);
    }

    @Test
    void shouldMarkSessionAsDone() throws Exception {

        Long sessionId = 1L;
        FocusSessionDTO focusSessionDTO = TestDataFactory.createFocusSessionDTO(sessionId, EStatus.DONE, 30L);

        when(focusSessionService.markFocusSessionAsDone(sessionId)).thenReturn(focusSessionDTO);

        MockCookie accessTokenCookie = new MockCookie("accessToken", "fake_secret_jwt_GFHjF7GkJrZeR7bLxXBtZZtS");

        mockMvc.perform(put("/api/v1/sessions/status/done/{sessionId}", sessionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId))
                .andExpect(jsonPath("$.status").value(EStatus.DONE.name()));

        verify(focusSessionService).markFocusSessionAsDone(sessionId);

    }
}