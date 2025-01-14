package fr.focusflow.services;

import fr.focusflow.dtos.TaskDTO;
import fr.focusflow.entities.EStatus;
import fr.focusflow.exceptions.TaskNotFoundException;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    TaskDTO save(TaskDTO newTaskDTO);

    List<TaskDTO> findAllTasksByUserIdAndStatus(Long UserId, EStatus status);

    List<TaskDTO> findAllTasksByUserId(Long userId);

    Optional<TaskDTO> findTaskById(Long taskId);

    TaskDTO updateTask(Long id, TaskDTO taskDTO) throws TaskNotFoundException;

    void deleteTask(Long id);

    List<TaskDTO> updateStatusOfAllTasks(List<TaskDTO> taskListToUpdate);
}
