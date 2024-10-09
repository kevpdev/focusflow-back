package fr.focusflow.controllers;

import fr.focusflow.TestDataFactory;
import fr.focusflow.TestUtil;
import fr.focusflow.entities.Task;
import fr.focusflow.security.CustomUserDetailsService;
import fr.focusflow.security.JwtTokenProvider;
import fr.focusflow.security.SecurityConfig;
import fr.focusflow.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    private Logger logger = LoggerFactory.getLogger(TaskControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private Task taskRequestBody;
    private Task taskResponseBody;


    @BeforeEach
    void setUp() {
        taskRequestBody = TestDataFactory.createRequestBodyTask();

        taskResponseBody = Task.builder()
                .id(1L)  // simule la génération de l'id apres la sauvegarde
                .title(taskRequestBody.getTitle())
                .description(taskRequestBody.getDescription())
                .priority(taskRequestBody.getPriority())
                .status(taskRequestBody.getStatus())
                .user(taskRequestBody.getUser()) // Attention si l'utilisateur est mutable
                .dueDate(taskRequestBody.getDueDate())
                .build();
    }

    @Test
    @WithMockUser(username = "toto", roles = {"USER"})
    public void shouldReturnSavedNewTask() throws Exception {

        // Simuler un token valide
        String token = "fake-token";

        // Simuler la réponse du service
        when(taskService.save(any(Task.class))).thenReturn(taskResponseBody);

        String jsonRequestBody = TestUtil.objectToJsonMapper(taskRequestBody);

        // Simulation appel rest controller
        mockMvc.perform(post("/api/tasks")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(taskResponseBody.getTitle()))
                .andExpect(jsonPath("$.description").value(taskResponseBody.getDescription()))
                .andExpect(jsonPath("$.priority").value(taskResponseBody.getPriority()))
                .andExpect(jsonPath("$.status").value(taskResponseBody.getStatus().name()))
                .andExpect(jsonPath("$.user.username").value("toto"))
                .andExpect(jsonPath("$.user.email").value("toto@gmail.com"))
                .andExpect(jsonPath("$.user.roles[0].name").value("USER"));

    }

}