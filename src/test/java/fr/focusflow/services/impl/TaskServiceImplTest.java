package fr.focusflow.services.impl;

import fr.focusflow.TestDataFactory;
import fr.focusflow.dtos.TaskDTO;
import fr.focusflow.entities.Task;
import fr.focusflow.mappers.TaskMapper;
import fr.focusflow.repositories.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    Logger logger = LoggerFactory.getLogger(TaskServiceImplTest.class);
    private Task task;
    private List<Task> taskList;
    @Mock
    private TaskRepository taskRepository;

    @Spy
    private TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);

    @InjectMocks
    private TaskServiceImpl taskService;


    @BeforeEach
    void setUp() {


        task = TestDataFactory.createDefaultTask(1L, TestDataFactory.createUser());
        taskList = TestDataFactory.createTaskList();
    }

    @Test
    void shouldSaveTaskWithSuccess() {

        logger.info("Debut test : shouldSaveTaskWithSuccess");

        //Simuler appel service et retour
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO taskDTOToUpdate = taskMapper.mapTaskToTaskDTO(task);

        // récuperer le retour
        TaskDTO savedTaskDTO = taskService.save(taskDTOToUpdate);

        logger.info("Tache créée : " + savedTaskDTO);

        // assert non null et data
        Assertions.assertNotNull(savedTaskDTO);
        Assertions.assertEquals(1L, savedTaskDTO.id());
        Assertions.assertEquals("Faire la vaisselle", savedTaskDTO.title());

        //verification appel repository
        verify(taskRepository).save(taskMapper.mapTaskDTOToTask(savedTaskDTO));

        logger.info("Fin test : shouldSaveTaskWithSuccess");


    }

    @Test
    public void shouldFindAllTasks() {

        when(taskRepository.findByUserId(2L)).thenReturn(taskList);
        List<TaskDTO> taskDTOList = taskService.findAllTasksByUserId(2L);

        //Assertion
        Assertions.assertNotNull(taskDTOList);
        Assertions.assertEquals(2, taskDTOList.size());

        // Verification de l'appel
        verify(taskRepository).findByUserId(2L);

    }
}