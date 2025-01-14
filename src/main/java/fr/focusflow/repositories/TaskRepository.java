package fr.focusflow.repositories;

import fr.focusflow.entities.EStatus;
import fr.focusflow.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);

    List<Task> findAllByOrderByPriorityAscDueDateAsc();

    List<Task> findByUserIdAndStatus(Long userId, EStatus status);

    @Modifying
    @Query("UPDATE Task t SET t.status = :status WHERE t.id IN :taskIds")
    int updateStatusOfAllTasks(@Param("status") String status, @Param("taskIds") List<Long> taskIds);

    @Query("SELECT t FROM Task t WHERE t.id IN :taskIds")
    List<Task> findTasksByIds(@Param("taskIds") List<Long> taskIds);


}
