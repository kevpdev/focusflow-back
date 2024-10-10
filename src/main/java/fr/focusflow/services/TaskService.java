package fr.focusflow.services;

import fr.focusflow.entities.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    public Task save(Task newTask);

    public List<Task> findAll();

    List<Task> getUserTasks(Long userId);

    Optional<Task> getTaskById(Long taskId);
}
