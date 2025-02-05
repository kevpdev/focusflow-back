package fr.focusflow.validations;

import fr.focusflow.entities.EStatus;
import fr.focusflow.entities.FocusSession;
import fr.focusflow.exceptions.FocusSessionStatusException;
import org.springframework.stereotype.Component;

@Component
public class FocusSessionStatusValidator {

    private static final String THE_SESSION_STATUS_IS_ALREADY = "The session status is already ";
    private static final String CANNOT_MARK_DONE_SESSION_AS = "Unable to mark a completed session as ";

    /**
     * Validate the transition of the session status from the current status to a new status.
     *
     * @param newStatus       new status
     * @param existingSession current session
     * @throws FocusSessionStatusException if the transition is not valid
     */
    public void validateStatusTransition(EStatus newStatus, FocusSession existingSession) throws FocusSessionStatusException {

        EStatus currentStatus = existingSession.getStatus();

        if (currentStatus == EStatus.PENDING && newStatus == EStatus.PENDING ||
                currentStatus == EStatus.IN_PROGRESS && newStatus == EStatus.IN_PROGRESS) {
            throw new FocusSessionStatusException(THE_SESSION_STATUS_IS_ALREADY + newStatus);
        }

        if (currentStatus == EStatus.DONE) {
            throw new FocusSessionStatusException(CANNOT_MARK_DONE_SESSION_AS + newStatus);
        }
    }
}
