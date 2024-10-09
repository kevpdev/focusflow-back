package fr.focusflow.services.impl;

import fr.focusflow.entities.ERole;
import fr.focusflow.entities.Role;
import fr.focusflow.repositories.RoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    Logger logger = LoggerFactory.getLogger(RoleServiceImplTest.class);

    private Role role;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        this.role = Role.builder()
                .name(ERole.USER.name()).build();
    }


    @Test
    public void shouldReturnRoleByNameTest() {

        logger.info("Debut shouldReturnRoleByNameTest");

        // Mock appel ROle
        when(roleRepository.findByName(ERole.USER.name())).thenReturn(Optional.of(this.role));

        Role foundRole = roleService.findByName(ERole.USER.name()).orElse(null);

        // Test Data
        Assertions.assertNotNull(foundRole);
        Assertions.assertEquals(this.role.getName(), foundRole.getName());

        // Verification appel service
        verify(roleRepository).findByName(ERole.USER.name());

        logger.info("Fin shouldReturnRoleByNameTest");
    }
}