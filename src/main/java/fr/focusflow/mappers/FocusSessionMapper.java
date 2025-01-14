package fr.focusflow.mappers;

import fr.focusflow.dtos.FocusSessionDTO;
import fr.focusflow.entities.FocusSession;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FocusSessionMapper {

    FocusSessionDTO mapFocusSessionToFocusDTO(FocusSession focusSession);
}
