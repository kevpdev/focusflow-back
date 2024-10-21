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
@RequestMapping("/api/v1/tasks")
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
    public ResponseEntity<List<Task>> getAllUserTask(Authentication authentication) {

        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        List<Task> userTasks = taskService.getUserTasks(currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(userTasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) throws TaskNotFoundException {
        Task task = taskService.getTaskById(id).orElseThrow(() -> new TaskNotFoundException("Task not found !"));
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) throws TaskNotFoundException {
        Task updatedTask = taskService.updateTask(id, task);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> updateTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> markTaskAsCompleted(@PathVariable Long id) throws TaskNotFoundException {
        Task updatedTask = taskService.markTaskAsCompleted(id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }


}
