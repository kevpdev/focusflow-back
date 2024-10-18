package fr.focusflow.services.impl;

import fr.focusflow.entities.Task;
import fr.focusflow.exceptions.TaskNotFoundException;
import fr.focusflow.mappers.TaskMapper;
import fr.focusflow.repositories.TaskRepository;
import fr.focusflow.services.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {


    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public Task save(Task newTask) {
        return taskRepository.save(newTask);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getUserTasks(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    @Override
    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    public Task updateTask(Long id, Task task) throws TaskNotFoundException {

        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task Not found !"));

        taskMapper.updateTask(task, existingTask);

        return taskRepository.save(existingTask);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }


}
