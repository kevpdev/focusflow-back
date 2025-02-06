package fr.focusflow.mappers;

import fr.focusflow.dtos.FocusSessionDTO;
import fr.focusflow.entities.FocusSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FocusSessionMapper {

    @Mapping(target = "userId", expression = "java(MapperUtil.mapUserToUserId(focusSession.getUser()))")
    @Mapping(target = "taskId", expression = "java(MapperUtil.mapTaskToTaskId(focusSession.getTask()))")
    FocusSessionDTO mapFocusSessionToFocusDTO(FocusSession focusSession);
}
