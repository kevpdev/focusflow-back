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
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    private static final String API_TASKS_ID = "/api/tasks/{id}";
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
    private String authorizationHeader;


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

        authorizationHeader = "Bearer eykds5fsdg55sf5sdf5sf5sdf5sf_fake_token";

        // initialisatio context spring pour le bean authentication
        TestDataFactory.setUpSecurityContext();


    }


    @Test
    @WithMockUser(username = "toto", roles = {"USER"})
    public void shouldReturnSavedNewTask() throws Exception {

        // Simuler la réponse du service
        when(taskService.save(any(Task.class))).thenReturn(taskResponseBody);

        String jsonRequestBody = TestUtil.objectToJsonMapper(taskRequestBody);

        // Simulation appel rest controller
        mockMvc.perform(post("/api/tasks")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authorizationHeader))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(taskResponseBody.getTitle()))
                .andExpect(jsonPath("$.description").value(taskResponseBody.getDescription()))
                .andExpect(jsonPath("$.priority").value(taskResponseBody.getPriority()))
                .andExpect(jsonPath("$.status").value(taskResponseBody.getStatus().name()))
                .andExpect(jsonPath("$.user.username").value("toto"))
                .andExpect(jsonPath("$.user.email").value("toto@gmail.com"))
                .andExpect(jsonPath("$.user.roles[0].name").value("USER"));

    }

    @Test
    @WithMockUser(username = "toto", roles = {"USER"})
    public void shouldReturnTaskListByUserId() throws Exception {

        List<Task> userTaskList = TestDataFactory.createTaskList();

        when(taskService.getUserTasks(any(Long.class))).thenReturn(userTaskList);

        mockMvc.perform(get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "toto", roles = {"USER"})
    public void shouldReturnTaskById() throws Exception {

        Task task = TestDataFactory.createTask(2L, TestDataFactory.createUser());
        when(taskService.getTaskById(any(Long.class))).thenReturn(Optional.of(task));

        mockMvc.perform(get(API_TASKS_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2"));

    }

    @Test
    @WithMockUser(username = "toto", roles = {"USER"})
    public void shouldReturn404ErrorWhenTaskNotFound() throws Exception {

        mockMvc.perform(get(API_TASKS_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isNotFound());

    }

    @Test
    public void shouldReturnUpdatedTask() throws Exception {

        Task task = TestDataFactory.createTask(1L, TestDataFactory.createUser());
        task.setTitle("Ranger la chambre");
        task.setDescription("Ranger la chambre avant le soir");

        when(taskService.updateTask(any(Long.class), any(Task.class))).thenReturn(task);

        mockMvc.perform(put(API_TASKS_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader)
                        .content(TestUtil.objectToJsonMapper(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Ranger la chambre"))
                .andExpect(jsonPath("$.description").value("Ranger la chambre avant le soir"));

    }

    @Test
    public void shouldDeleteTask() throws Exception {

        Mockito.doNothing().when(taskService).deleteTask(any(Long.class));

        mockMvc.perform(delete(API_TASKS_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk());

    }

}