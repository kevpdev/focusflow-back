package fr.focusflow.controllers;

import fr.focusflow.entities.Task;
import fr.focusflow.exceptions.TaskNotFoundException;
import fr.focusflow.security.CustomUserDetails;
import fr.focusflow.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;
    private Logger logger = LoggerFactory.getLogger(LoggerFactory.class);

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Create a new task", description = "Creates a new task and returns it with its generated ID.")
    @ApiResponse(responseCode = "201", description = "Task successfully created")
    @PostMapping
    public ResponseEntity<Task> saveTask(@RequestBody Task task) {
        Task savedTask = taskService.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @Operation(summary = "Get all tasks for the current user", description = "Fetches all tasks assigned to the authenticated user.")
    @ApiResponse(responseCode = "200", description = "List of tasks successfully retrieved")
    @GetMapping
    public ResponseEntity<List<Task>> getAllUserTask(Authentication authentication) {
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        List<Task> userTasks = taskService.getUserTasks(currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(userTasks);
    }

    @Operation(summary = "Retrieve a specific task by ID", description = "Fetches the details of a task by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(
            @Parameter(description = "ID of the task to be retrieved") @PathVariable Long id) throws TaskNotFoundException {
        Task task = taskService.getTaskById(id).orElseThrow(() -> new TaskNotFoundException("Task not found!"));
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @Operation(summary = "Update an existing task", description = "Updates the details of an existing task by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task successfully updated"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @Parameter(description = "ID of the task to be updated") @PathVariable Long id,
            @RequestBody Task task) throws TaskNotFoundException {
        Task updatedTask = taskService.updateTask(id, task);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }

    @Operation(summary = "Delete a task", description = "Deletes a task by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@Parameter(description = "ID of the task to be deleted") @PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Mark a task as completed", description = "Marks a task as completed by updating its status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task successfully marked as completed"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> markTaskAsCompleted(
            @Parameter(description = "ID of the task to be marked as completed") @PathVariable Long id) throws TaskNotFoundException {
        Task updatedTask = taskService.markTaskAsCompleted(id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }
}
