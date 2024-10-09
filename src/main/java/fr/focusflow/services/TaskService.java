package fr.focusflow.services;

import fr.focusflow.entities.Task;

import java.util.List;

public interface TaskService {

    public Task save(Task newTask);

    public List<Task> findAll();
}
