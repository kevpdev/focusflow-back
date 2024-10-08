package fr.focusflow.services.impl;

import fr.focusflow.models.Task;
import fr.focusflow.repositories.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    static Task task;
    Logger logger = LoggerFactory.getLogger(TaskServiceImplTest.class);
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;


    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(1L)
                .title("Faire la vaisselle")
                .description("Faire la vaisselle avant samedi")
                .build();
    }

    @Test
    void shouldSaveTaskWithSuccess() {

        logger.info("Debut test : shouldSaveTaskWithSuccess");

        //Simuler appel service et retour
        when(taskRepository.save(task)).thenReturn(task);

        // récuperer le retour
        Task savedTask = taskService.save(task);

        logger.info("Tache créée : " + savedTask);

        // assert non null et data
        Assertions.assertNotNull(savedTask);
        Assertions.assertEquals(1L, task.getId());
        Assertions.assertEquals("Faire la vaisselle", task.getTitle());

        //verification appel repository
        verify(taskRepository).save(savedTask);

        logger.info("Fin test : shouldSaveTaskWithSuccess");


    }
}