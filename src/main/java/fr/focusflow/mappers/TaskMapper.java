package fr.focusflow.mappers;

import fr.focusflow.dtos.TaskDTO;
import fr.focusflow.entities.Task;
import fr.focusflow.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "user", expression = "java(mapUserIdToUser(source.userId()))")
    Task updateTask(TaskDTO source, @MappingTarget Task target);

    @Mapping(target = "userId", expression = "java(mapUserToUserId(task.getUser()))")
    TaskDTO mapTaskToTaskDTO(Task task);

    @Mapping(target = "user", expression = "java(mapUserIdToUser(taskDTO.userId()))")
    Task mapTaskDTOToTask(TaskDTO taskDTO);

    /**
     * Méthode utilitaire pour mapper un userId en User.
     *
     * @param userId l'identifiant de l'utilisateur
     * @return un objet User avec seulement l'ID défini
     */
    default User mapUserIdToUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    default Long mapUserToUserId(User user) {
        if (user == null) {
            return null;
        }
        return user.getId();
    }
}