package fr.focusflow.mappers;

import fr.focusflow.entities.Task;
import fr.focusflow.entities.User;

public class MapperUtil {

    /**
     * Méthode utilitaire pour mapper un userId en User.
     *
     * @param userId l'identifiant de l'utilisateur
     * @return un objet User avec seulement l'ID défini
     */
    public static User mapUserIdToUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    public static Long mapUserToUserId(User user) {
        if (user == null) {
            return null;
        }
        return user.getId();
    }

    public static Long mapTaskToTaskId(Task task) {
        if (task == null) {
            return null;
        }
        return task.getId();
    }

    public static Task mapTaskIdToTask(Long taskId) {
        if (taskId == null) {
            return null;
        }
        Task task = new Task();
        task.setId(taskId);
        return task;
    }
}
