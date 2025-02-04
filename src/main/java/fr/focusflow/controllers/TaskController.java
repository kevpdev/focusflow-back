package fr.focusflow.controllers;

import fr.focusflow.dtos.TaskDTO;
import fr.focusflow.entities.EStatus;
import fr.focusflow.exceptions.TaskNotFoundException;
import fr.focusflow.security.CustomUserDetails;
import fr.focusflow.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @Operation(summary = "Get all tasks for the current user", description = "Fetches all tasks assigned to the authenticated user.")
    @ApiResponse(responseCode = "200", description = "List of tasks successfully retrieved")
    @GetMapping
    public ResponseEntity<List<TaskDTO>> findAllTasksByUserId(Authentication authentication) {
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        List<TaskDTO> tasksByUserId = taskService.findAllTasksByUserId(currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(tasksByUserId);
    }

    @Operation(summary = "Retrieve a specific task by ID", description = "Fetches the details of a task by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(
            @Parameter(description = "ID of the task to be retrieved") @PathVariable Long id) throws TaskNotFoundException {
        TaskDTO task = taskService.findTaskById(id).orElseThrow(() -> new TaskNotFoundException("Task not found!"));
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }


    @Operation(summary = "Get all tasks for the current user by status", description = "Fetches all tasks assigned to the authenticated user by status.")
    @ApiResponse(responseCode = "200", description = "List of tasks successfully retrieved")
    @GetMapping("/search")
    public ResponseEntity<List<TaskDTO>> findAllTasksByUserIdAndStatus(Authentication authentication, @RequestParam("status") EStatus status) {
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
        List<TaskDTO> userTasks = taskService.findAllTasksByUserIdAndStatus(currentUser.getId(), status);
        return ResponseEntity.status(HttpStatus.OK).body(userTasks);
    }

    @Operation(summary = "Create a new task", description = "Creates a new task and returns it with its generated ID.")
    @ApiResponse(responseCode = "201", description = "Task successfully created")
    @PostMapping
    public ResponseEntity<TaskDTO> saveTask(@RequestBody TaskDTO taskDTO, Authentication authentication) {

        // Récupération de l'utilisateur connecté
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

        // Construction de la nouvelle tâche avec l'userId
        TaskDTO newTask = TaskDTO.builder()
                .title(taskDTO.title())
                .description(taskDTO.description())
                .status(taskDTO.status())
                .priority(taskDTO.priority())
                .dueDate(taskDTO.dueDate())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .userId(currentUser.getId())  // Association de l'utilisateur
                .build();

        TaskDTO savedTask = taskService.save(newTask);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }


    @Operation(summary = "Update an existing task", description = "Updates the details of an existing task by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task successfully updated"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @Parameter(description = "ID of the task to be updated") @PathVariable Long id,
            @RequestBody TaskDTO taskDTO) throws TaskNotFoundException {
        TaskDTO modifiedTask = taskService.updateTask(id, taskDTO);
        return ResponseEntity.status(HttpStatus.OK).body(modifiedTask);
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

    @Operation(summary = "Update status of all tasks", description = "Update status of all tasks and return all modified tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modified tasks successfully"),
            @ApiResponse(responseCode = "404", description = "Tasks not found")
    })
    @PutMapping("/status")
    public ResponseEntity<List<TaskDTO>> updateStatusOfAllTasks(@RequestBody List<TaskDTO> tasksToUpdate) {
        List<TaskDTO> modifiedTask = taskService.updateStatusOfAllTasks(tasksToUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(modifiedTask);
    }
}
