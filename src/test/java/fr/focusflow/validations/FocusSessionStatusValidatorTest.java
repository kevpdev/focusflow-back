package fr.focusflow.validations;

import fr.focusflow.TestDataFactory;
import fr.focusflow.entities.EStatus;
import fr.focusflow.entities.FocusSession;
import fr.focusflow.exceptions.FocusSessionStatusException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FocusSessionStatusValidatorTest {

    FocusSessionStatusValidator focusSessionStatusValidator;

    @BeforeEach
    void setup() {
        focusSessionStatusValidator = new FocusSessionStatusValidator();
    }

    @Test
    public void shouldAllowTransitionStatusFromPendingToInProgess() {

        FocusSession existingSession = TestDataFactory.createFocusSession(TestDataFactory.createFocusSessionDTO(1L));
        existingSession.setStatus(EStatus.PENDING);
        Assertions.assertDoesNotThrow(() -> focusSessionStatusValidator.validateStatusTransition(EStatus.IN_PROGRESS, existingSession));
    }

    @Test
    public void shouldAllowTransitionStatusFromInProgressToPending() {

        FocusSession existingSession = TestDataFactory.createFocusSession(TestDataFactory.createFocusSessionDTO(1L));
        Assertions.assertDoesNotThrow(() -> focusSessionStatusValidator.validateStatusTransition(EStatus.PENDING, existingSession));
    }

    @Test
    public void shouldAllowTransitionStatusFromInProgressToDone() {

        FocusSession existingSession = TestDataFactory.createFocusSession(TestDataFactory.createFocusSessionDTO(1L));
        Assertions.assertDoesNotThrow(() -> focusSessionStatusValidator.validateStatusTransition(EStatus.DONE, existingSession));
    }

    @Test
    public void shouldThrowExceptionIfTryingToChangeDoneStatusToAnyStatus() {

        FocusSession existingSession = TestDataFactory.createFocusSession(TestDataFactory.createFocusSessionDTO(1L));
        existingSession.setStatus(EStatus.DONE);
        assertThrowAllSessionStatus(existingSession);
    }

    private void assertThrowAllSessionStatus(FocusSession existingSession) {
        Assertions.assertThrows(FocusSessionStatusException.class,
                () -> focusSessionStatusValidator.validateStatusTransition(EStatus.DONE, existingSession));
        Assertions.assertThrows(FocusSessionStatusException.class,
                () -> focusSessionStatusValidator.validateStatusTransition(EStatus.IN_PROGRESS, existingSession));
        Assertions.assertThrows(FocusSessionStatusException.class,
                () -> focusSessionStatusValidator.validateStatusTransition(EStatus.PENDING, existingSession));
    }

}