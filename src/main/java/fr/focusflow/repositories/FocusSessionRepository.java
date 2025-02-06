package fr.focusflow.repositories;

import fr.focusflow.entities.FocusSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {

    @Query("SELECT fs FROM FocusSession fs WHERE fs.user.id = :userId AND fs.status <> 'DONE'")
    Optional<FocusSession> findSessionActiveByUserId(@Param("userId") Long userId);
}
