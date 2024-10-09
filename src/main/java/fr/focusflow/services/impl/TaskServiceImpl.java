package fr.focusflow.services.impl;

import fr.focusflow.entities.Task;
import fr.focusflow.repositories.TaskRepository;
import fr.focusflow.services.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {


    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task save(Task newTask) {
        return taskRepository.save(newTask);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }
}
