package fr.focusflow.services;

import fr.focusflow.entities.Role;

import java.util.Optional;

public interface RoleService {

    Optional<Role> findByName(String name);
}
