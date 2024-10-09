package fr.focusflow.controllers;

import fr.focusflow.entities.Task;
import fr.focusflow.services.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private Logger logger = LoggerFactory.getLogger(LoggerFactory.class);

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Task> saveTask(@RequestBody Task task) {
        logger.info("Task param: {}", task);
        Task savedTask = taskService.save(task);
        logger.info("Task saved: {}", savedTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }


}
