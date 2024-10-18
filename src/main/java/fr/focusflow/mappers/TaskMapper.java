package fr.focusflow.mappers;

import fr.focusflow.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task updateTask(Task source, @MappingTarget Task target);
}
