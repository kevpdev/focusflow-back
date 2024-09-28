package fr.focusflow.services;

import fr.focusflow.Models.Role;

import java.util.Optional;

public interface RoleService {

    Optional<Role> findByName(String name);
}
