package fr.focusflow.controllers;

import fr.focusflow.entities.Task;
import fr.focusflow.exceptions.TaskNotFoundException;
import fr.focusflow.security.CustomUserDetails;
import fr.focusflow.services.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<Task>> getUserTask(Authentication authentication) {

        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        List<Task> userTasks = taskService.getUserTasks(currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(userTasks);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) throws TaskNotFoundException {
        Task task = taskService.getTaskById(id).orElseThrow(() -> new TaskNotFoundException("Task not found !"));
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }


}
