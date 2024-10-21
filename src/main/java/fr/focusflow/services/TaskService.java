package fr.focusflow.services;

import fr.focusflow.entities.Task;
import fr.focusflow.exceptions.TaskNotFoundException;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    Task save(Task newTask);

    List<Task> findAll();

    List<Task> getUserTasks(Long userId);

    Optional<Task> getTaskById(Long taskId);

    Task updateTask(Long id, Task task) throws TaskNotFoundException;

    void deleteTask(Long id);

    Task markTaskAsCompleted(Long id) throws TaskNotFoundException;
}
