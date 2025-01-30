package fr.focusflow.services.impl;

import fr.focusflow.dtos.TaskDTO;
import fr.focusflow.entities.EStatus;
import fr.focusflow.entities.Task;
import fr.focusflow.exceptions.TaskNotFoundException;
import fr.focusflow.mappers.TaskMapper;
import fr.focusflow.repositories.TaskRepository;
import fr.focusflow.services.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

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
    public TaskDTO save(TaskDTO newTaskDTO) {
        Task newTask = taskMapper.mapTaskDTOToTask(newTaskDTO);
        return taskMapper.mapTaskToTaskDTO(taskRepository.save(newTask));
    }

    /**
     * Find all tasks by status
     *
     * @param status
     * @return Task List by status
     */
    @Override
    public List<TaskDTO> findAllTasksByUserIdAndStatus(Long userId, EStatus status) {
        return taskRepository.findByUserIdAndStatus(userId, status)
                .stream().map(taskMapper::mapTaskToTaskDTO)
                .toList();
    }

    /**
     * Get all user's tasks
     *
     * @param userId
     * @return Task object List
     */
    @Override
    public List<TaskDTO> findAllTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId)
                .stream()
                .map(taskMapper::mapTaskToTaskDTO)
                .toList();
    }

    @Override
    public Optional<TaskDTO> findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .map(taskMapper::mapTaskToTaskDTO);
    }

    @Override
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) throws TaskNotFoundException {

        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

        Task modifiedTask = taskMapper.updateTask(taskDTO, existingTask);
        return taskMapper.mapTaskToTaskDTO(taskRepository.save(modifiedTask));
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    /**
     * @param taskListToUpdate
     * @return
     */
    @Transactional
    @Override
    public List<TaskDTO> updateStatusOfAllTasks(List<TaskDTO> taskListToUpdate) {

        if (taskListToUpdate == null || taskListToUpdate.isEmpty()) {
            return Collections.emptyList();
        }

        //regrouper les taches par status
        Map<EStatus, List<Long>> mapTaskByStatus = taskListToUpdate
                .stream()
                .collect(groupingBy(TaskDTO::status, Collectors.mapping(TaskDTO::id, Collectors.toList())));

        // Mettre à jour pour chaque liste
        mapTaskByStatus.forEach(taskRepository::updateStatusOfAllTasks);

        // récupération des taches
        List<Long> allTaskIdList = taskListToUpdate.stream().map(TaskDTO::id).toList();

        return taskRepository.findTasksByIds(allTaskIdList)
                .stream().map(taskMapper::mapTaskToTaskDTO)
                .toList();
    }

}
