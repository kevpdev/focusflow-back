package fr.focusflow.mappers;

import fr.focusflow.dtos.TaskDTO;
import fr.focusflow.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "user", expression = "java(MapperUtil.mapUserIdToUser(source.userId()))")
    Task updateTask(TaskDTO source, @MappingTarget Task target);

    @Mapping(target = "userId", expression = "java(MapperUtil.mapUserToUserId(task.getUser()))")
    TaskDTO mapTaskToTaskDTO(Task task);

    @Mapping(target = "user", expression = "java(MapperUtil.mapUserIdToUser(taskDTO.userId()))")
    Task mapTaskDTOToTask(TaskDTO taskDTO);

}