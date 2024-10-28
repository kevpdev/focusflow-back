package fr.focusflow.controllers;

import fr.focusflow.TestDataFactory;
import fr.focusflow.TestUtil;
import fr.focusflow.dtos.FocusSessionRequestDTO;
import fr.focusflow.entities.EFocusSessionStatus;
import fr.focusflow.entities.FocusSession;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    public void shouldResumeSession() throws Exception {
        shouldStartOrResumeSession(1L);
    }

    @Test
    public void shouldStartNewSession() throws Exception {
        shouldStartOrResumeSession(null);
    }

    /**
     * Test start new session if sessionId is null or resume existing session
     *
     * @param sessionId start new session if sessionId null
     * @throws Exception
     */
    private void shouldStartOrResumeSession(Long sessionId) throws Exception {
        FocusSession focusSession = TestDataFactory.createFocusSession(sessionId);
        focusSession.setId(1L);

        when(focusSessionService.startOrResumeSession(1L, sessionId)).thenReturn(focusSession);


        FocusSessionRequestDTO focusSessionRequestDTO = new FocusSessionRequestDTO(1L, sessionId);
        String jsonContent = TestUtil.objectToJsonMapper(focusSessionRequestDTO);

        mockMvc.perform(put("/api/v1/sessions/status/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(focusSession.getId()))
                .andExpect(jsonPath("$.status").value(EFocusSessionStatus.IN_PROGRESS.name()));

        verify(focusSessionService).startOrResumeSession(1L, sessionId);
    }

    @Test
    public void shouldMarkSessionAsPending() throws Exception {

        Long sessionId = 1L;
        FocusSession focusSession = TestDataFactory.createFocusSession(sessionId);
        focusSession.setStatus(EFocusSessionStatus.PENDING);

        when(focusSessionService.markFocusSessionAsPending(sessionId)).thenReturn(focusSession);

        mockMvc.perform(put("/api/v1/sessions/status/pending/{sessionId}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId))
                .andExpect(jsonPath("$.status").value(EFocusSessionStatus.PENDING.name()));

        verify(focusSessionService).markFocusSessionAsPending(sessionId);
    }

    @Test
    public void shouldMarkSessionAsDone() throws Exception {

        Long sessionId = 1L;
        FocusSession focusSession = TestDataFactory.createFocusSession(sessionId);
        focusSession.setStatus(EFocusSessionStatus.DONE);

        when(focusSessionService.markFocusSessionAsDone(sessionId)).thenReturn(focusSession);

        mockMvc.perform(put("/api/v1/sessions/status/done/{id}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId))
                .andExpect(jsonPath("$.status").value(EFocusSessionStatus.DONE.name()));

        verify(focusSessionService).markFocusSessionAsDone(sessionId);

    }

    @Test
    public void shouldMarkSessionAsCancelled() throws Exception {

        Long sessionId = 1L;
        FocusSession focusSession = TestDataFactory.createFocusSession(sessionId);
        focusSession.setStatus(EFocusSessionStatus.CANCELLED);

        when(focusSessionService.markFocusSessionAsCancelled(sessionId)).thenReturn(focusSession);

        mockMvc.perform(put("/api/v1/sessions/status/cancelled/{id}", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId))
                .andExpect(jsonPath("$.status").value(EFocusSessionStatus.CANCELLED.name()));

        verify(focusSessionService).markFocusSessionAsCancelled(sessionId);
    }
}