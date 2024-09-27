package fr.focusflow.repositories;

import fr.focusflow.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existByEmail(String email);

    Optional<User> findByEmail(String email);
}
