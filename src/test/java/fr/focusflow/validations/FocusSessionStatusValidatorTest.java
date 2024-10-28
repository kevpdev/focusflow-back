package fr.focusflow.validations;

import fr.focusflow.TestDataFactory;
import fr.focusflow.entities.EFocusSessionStatus;
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

        FocusSession existingSession = TestDataFactory.createFocusSession(1L);
        existingSession.setStatus(EFocusSessionStatus.PENDING);
        Assertions.assertDoesNotThrow(() -> focusSessionStatusValidator.validateStatusTransition(EFocusSessionStatus.IN_PROGRESS, existingSession));
    }

    @Test
    public void shouldAllowTransitionStatusFromInProgressToPending() {

        FocusSession existingSession = TestDataFactory.createFocusSession(1L);
        Assertions.assertDoesNotThrow(() -> focusSessionStatusValidator.validateStatusTransition(EFocusSessionStatus.PENDING, existingSession));
    }

    @Test
    public void shouldAllowTransitionStatusFromInProgressToDoneOrCancelled() {

        FocusSession existingSession = TestDataFactory.createFocusSession(1L);
        Assertions.assertDoesNotThrow(() -> focusSessionStatusValidator.validateStatusTransition(EFocusSessionStatus.DONE, existingSession));
        Assertions.assertDoesNotThrow(() -> focusSessionStatusValidator.validateStatusTransition(EFocusSessionStatus.CANCELLED, existingSession));
    }

    @Test
    public void shouldThrowExceptionIfTryingToChangeDoneStatusToAnyStatus() {

        FocusSession existingSession = TestDataFactory.createFocusSession(1L);
        existingSession.setStatus(EFocusSessionStatus.DONE);
        assertThrowAllSessionStatus(existingSession);
    }

    @Test
    public void shouldThrowExceptionIfTryingToChangeCancelledStatusToAnyStatus() {

        FocusSession existingSession = TestDataFactory.createFocusSession(1L);
        existingSession.setStatus(EFocusSessionStatus.CANCELLED);
        assertThrowAllSessionStatus(existingSession);
    }

    private void assertThrowAllSessionStatus(FocusSession existingSession) {
        Assertions.assertThrows(FocusSessionStatusException.class,
                () -> focusSessionStatusValidator.validateStatusTransition(EFocusSessionStatus.CANCELLED, existingSession));
        Assertions.assertThrows(FocusSessionStatusException.class,
                () -> focusSessionStatusValidator.validateStatusTransition(EFocusSessionStatus.DONE, existingSession));
        Assertions.assertThrows(FocusSessionStatusException.class,
                () -> focusSessionStatusValidator.validateStatusTransition(EFocusSessionStatus.IN_PROGRESS, existingSession));
        Assertions.assertThrows(FocusSessionStatusException.class,
                () -> focusSessionStatusValidator.validateStatusTransition(EFocusSessionStatus.PENDING, existingSession));
    }

}