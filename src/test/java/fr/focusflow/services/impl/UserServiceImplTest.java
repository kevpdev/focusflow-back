package fr.focusflow.services.impl;

import fr.focusflow.TestDataFactory;
import fr.focusflow.entities.User;
import fr.focusflow.repositories.UserRepository;
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
class UserServiceImplTest {

    Logger logger = LoggerFactory.getLogger(UserServiceImplTest.class);

    private User user;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        this.user = TestDataFactory.createUser();
    }

    @Test
    public void shouldReturnTrueIfUserExistsByEmailTest() {

        logger.info("Debut shouldReturnTrueIfUserExistsByEmailTest");

        //Mock appel repository
        when(userRepository.existsByEmail(this.user.getEmail())).thenReturn(true);

        // Test du résultat
        Assertions.assertTrue(userService.existByEmail(this.user.getEmail()));

        // Verification appel
        verify(userRepository).existsByEmail(this.user.getEmail());

        logger.info("Fin shouldReturnTrueIfUserExistsByEmailTest");
    }

    @Test
    public void shouldReturnUserWhenEmailExistTest() {
        logger.info("Debut shouldReturnUserWhenEmailExistTest");

        //Mock appel repository
        when(userRepository.findByEmail(this.user.getEmail())).thenReturn(Optional.of(this.user));

        User foundUser = userService.findByEmail(this.user.getEmail()).orElse(null);
        // Test du résultat
        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(this.user.getEmail(), foundUser.getEmail());
        Assertions.assertEquals(this.user.getId(), foundUser.getId());

        // Verification appel
        verify(userRepository).findByEmail(this.user.getEmail());

        logger.info("Fin shouldReturnUserWhenEmailExistTest");

    }

    @Test
    public void shouldReturnSavedUserTest() {

        logger.info("Debut shouldReturnSavedUserTest");

        //Mock appel repository
        when(userRepository.save(this.user)).thenReturn(this.user);

        // Récupération user ajouté
        User savedUser = userService.save(this.user);

        // Tests data
        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals(this.user.getId(), savedUser.getId());
        Assertions.assertEquals(this.user.getEmail(), savedUser.getEmail());

        // Verification appel service
        verify(userRepository).save(savedUser);

        logger.info("Fin shouldReturnSavedUserTest");
    }
}