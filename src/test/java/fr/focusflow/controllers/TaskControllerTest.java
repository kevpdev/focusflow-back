package fr.focusflow.controllers;

import fr.focusflow.TestDataFactory;
import fr.focusflow.TestUtil;
import fr.focusflow.dtos.TaskDTO;
import fr.focusflow.entities.EStatus;
import fr.focusflow.entities.Task;
import fr.focusflow.mappers.TaskMapper;
import fr.focusflow.mappers.TaskMapperImpl;
import fr.focusflow.security.CustomUserDetailsService;
import fr.focusflow.security.JwtTokenProvider;
import fr.focusflow.security.SecurityConfig;
import fr.focusflow.services.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SecurityConfig.class, TaskMapperImpl.class})
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    private static final String API_TASKS_ID = "/api/v1/tasks/{id}";
    private static final String API_TASK = "/api/v1/tasks";
    private Logger logger = LoggerFactory.getLogger(TaskControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskMapper taskMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private String authorizationHeader;

    @Captor
    private ArgumentCaptor<TaskDTO> taskDTOCaptor;

    @Captor
    private ArgumentCaptor<List<TaskDTO>> taskDTOListCaptor;


    @BeforeEach
    void setUp() {

        authorizationHeader = "Bearer eykds5fsdg55sf5sdf5sf5sdf5sf_fake_token";

        // initialisation context spring pour le bean authentication
        TestDataFactory.setUpSecurityContext();
    }


    @Test
    @WithMockUser(username = "toto", roles = {"USER"})
    public void shouldReturnSavedNewTask() throws Exception {

        TaskDTO taskRequestBody = TestDataFactory.createTaskDTO(null); // Pas encore d'ID
        TaskDTO taskResponseBody = TestDataFactory.createTaskDTO(1L); // La tâche sauvegardée avec un ID

        // Mock du service
        when(taskService.save(any(TaskDTO.class))).thenReturn(taskResponseBody);

        String jsonRequestBody = TestUtil.objectToJsonMapper(taskRequestBody);

        mockMvc.perform(post(API_TASK)
                        .with(csrf())
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(taskResponseBody.title()))
                .andExpect(jsonPath("$.description").value(taskResponseBody.description()))
                .andExpect(jsonPath("$.priority").value(taskResponseBody.priority()))
                .andExpect(jsonPath("$.status").value(taskResponseBody.status().name()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.dueDate").isNotEmpty())
                .andExpect(jsonPath("$.userId").value(taskResponseBody.userId()));

        // Capture l'objet passé au service
        verify(taskService).save(taskDTOCaptor.capture());
        TaskDTO capturedTask = taskDTOCaptor.getValue();

        // Assert sur l'objet capturé
        Assertions.assertEquals(taskRequestBody.title(), capturedTask.title());
        Assertions.assertEquals(taskRequestBody.description(), capturedTask.description());
        Assertions.assertEquals(taskRequestBody.priority(), capturedTask.priority());
        Assertions.assertEquals(taskRequestBody.status(), capturedTask.status());
        Assertions.assertEquals(taskRequestBody.userId(), capturedTask.userId());
    }


    @Test
    @WithMockUser(username = "toto", roles = {"USER"})
    public void shouldReturnTaskListByUserId() throws Exception {

        List<TaskDTO> tasksDTObyUserId = TestDataFactory.createTaskDTOList();

        when(taskService.findAllTasksByUserId(2L)).thenReturn(tasksDTObyUserId);

        mockMvc.perform(get(API_TASK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))


                .andExpect(jsonPath("$[0].id").value(tasksDTObyUserId.getFirst().id()))
                .andExpect(jsonPath("$[0].title").value(tasksDTObyUserId.getFirst().title()))
                .andExpect(jsonPath("$[0].description").value(tasksDTObyUserId.getFirst().description()))
                .andExpect(jsonPath("$[0].status").value(tasksDTObyUserId.getFirst().status().name()))

                .andExpect(jsonPath("$[1].id").value(tasksDTObyUserId.get(1).id()))
                .andExpect(jsonPath("$[1].title").value(tasksDTObyUserId.get(1).title()))
                .andExpect(jsonPath("$[1].description").value(tasksDTObyUserId.get(1).description()))
                .andExpect(jsonPath("$[1].status").value(tasksDTObyUserId.get(1).status().name()));


        verify(taskService).findAllTasksByUserId(2L);
    }

    @Test
    public void shouldReturnTaskById() throws Exception {

        TaskDTO task = TestDataFactory.createTaskDTO(2L);
        when(taskService.findTaskById(any(Long.class))).thenReturn(Optional.of(task));

        mockMvc.perform(get(API_TASKS_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.id()))
                .andExpect(jsonPath("$.title").value(task.title()))
                .andExpect(jsonPath("$.description").value(task.description()))
                .andExpect(jsonPath("$.status").value(task.status().toString()));

        verify(taskService).findTaskById(2L);
    }

    @Test
    public void shouldReturn404ErrorWhenTaskNotFound() throws Exception {

        mockMvc.perform(get(API_TASKS_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnUpdatedTask() throws Exception {

        TaskDTO taskDTO = TestDataFactory.createTaskDTO(1L);
        when(taskService.updateTask(anyLong(), any(TaskDTO.class))).thenReturn(taskDTO);

        mockMvc.perform(put(API_TASKS_ID, 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader)
                        .content(TestUtil.objectToJsonMapper(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskDTO.id()))
                .andExpect(jsonPath("$.title").value(taskDTO.title()))
                .andExpect(jsonPath("$.description").value(taskDTO.description()))
                .andExpect(jsonPath("$.status").value(taskDTO.status().name()))
                .andExpect(jsonPath("$.priority").value(taskDTO.priority()))
                .andExpect(jsonPath("$.dueDate").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.userId").value(taskDTO.userId()));

        verify(taskService).updateTask(eq(1L), taskDTOCaptor.capture());
        TaskDTO capturedTask = taskDTOCaptor.getValue();

        Assertions.assertEquals(taskDTO.id(), capturedTask.id());
        Assertions.assertEquals(taskDTO.title(), capturedTask.title());
        Assertions.assertEquals(taskDTO.description(), capturedTask.description());
        Assertions.assertEquals(taskDTO.status(), capturedTask.status());
        Assertions.assertEquals(taskDTO.priority(), capturedTask.priority());
        Assertions.assertEquals(taskDTO.updatedAt().toInstant(), capturedTask.updatedAt().toInstant());
        Assertions.assertEquals(taskDTO.createdAt().toInstant(), capturedTask.createdAt().toInstant());
        Assertions.assertEquals(taskDTO.dueDate().toInstant(), capturedTask.dueDate().toInstant());
        Assertions.assertEquals(taskDTO.userId(), capturedTask.userId());
    }

    @Test
    public void shouldDeleteTask() throws Exception {

        Mockito.doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete(API_TASKS_ID, 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk());

        verify(taskService).deleteTask(1L);

    }

    @Test
    public void shouldUpdateStatusOfAllTasks() throws Exception {


        List<Task> tasksToUpdate = TestDataFactory.createTaskList();
        tasksToUpdate.get(0).setStatus(EStatus.IN_PROGRESS);
        tasksToUpdate.get(1).setStatus(EStatus.DONE);

        List<TaskDTO> modifiedTasksDTO = tasksToUpdate
                .stream()
                .map(taskMapper::mapTaskToTaskDTO)
                .toList();

        // Mock du service
        when(taskService.updateStatusOfAllTasks(any())).thenReturn(modifiedTasksDTO);

        String jsonRequestBody = TestUtil.objectToJsonMapper(modifiedTasksDTO);

        mockMvc.perform(put(API_TASK + "/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorizationHeader)
                        .content(jsonRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(modifiedTasksDTO.get(0).id()))
                .andExpect(jsonPath("$[0].status").value(EStatus.IN_PROGRESS.name()))
                .andExpect(jsonPath("$[1].id").value(modifiedTasksDTO.get(1).id()))
                .andExpect(jsonPath("$[1].status").value(EStatus.DONE.name()));

        // Capture les arguments passés au service
        verify(taskService).updateStatusOfAllTasks(taskDTOListCaptor.capture());
        List<TaskDTO> capturedTasks = taskDTOListCaptor.getValue();

        // Assert sur les arguments capturés
        Assertions.assertEquals(2, capturedTasks.size());

        Assertions.assertEquals(modifiedTasksDTO.get(0).id(), capturedTasks.get(0).id());
        Assertions.assertEquals(modifiedTasksDTO.get(0).status(), capturedTasks.get(0).status());

        Assertions.assertEquals(modifiedTasksDTO.get(1).id(), capturedTasks.get(1).id());
        Assertions.assertEquals(modifiedTasksDTO.get(1).status(), capturedTasks.get(1).status());
    }

}