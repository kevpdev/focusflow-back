package fr.focusflow.services.impl;

import fr.focusflow.entities.ETaskStatus;
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


    private static final String TASK_NOT_FOUND_MESSAGE = "Task not found !";
    private static final String TASK_ALREADY_COMPLETED = "Task is already completed";
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

    /**
     * Get all user's tasks
     *
     * @param userId
     * @return Task object List
     */
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

        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

        taskMapper.updateTask(task, existingTask);

        return taskRepository.save(existingTask);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public Task markTaskAsCompleted(Long id) throws TaskNotFoundException {

        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

        if (ETaskStatus.DONE == existingTask.getStatus()) {
            throw new IllegalArgumentException(TASK_ALREADY_COMPLETED);
        }

        existingTask.setStatus(ETaskStatus.DONE);

        return taskRepository.save(existingTask);
    }


}
