package fr.focusflow.services.impl;

import fr.focusflow.models.Task;
import fr.focusflow.repositories.TaskRepository;
import fr.focusflow.services.TaskService;
import org.springframework.stereotype.Service;

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
}
