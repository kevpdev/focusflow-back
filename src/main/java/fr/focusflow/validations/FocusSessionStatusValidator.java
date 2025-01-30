package fr.focusflow.validations;

import fr.focusflow.entities.EStatus;
import fr.focusflow.entities.FocusSession;
import fr.focusflow.exceptions.FocusSessionStatusException;
import org.springframework.stereotype.Component;

@Component
public class FocusSessionStatusValidator {

    private static final String THE_SESSION_STATUS_IS_ALREADY = "The session status is already ";
    private static final String CANNOT_MARK_DONE_SESSION_AS = "Cannot mark done session as ";
    private static final String CANNOT_MARK_CANCELLED_SESSION_AS = "Cannot mark cancelled session as ";
    private static final String UNEXPECTED_SESSION_STATUS = "Unexpected session status : ";

    /**
     * Validate the transition of the session status from the current status to a new status.
     *
     * @param newStatus
     * @param existingSession
     * @throws FocusSessionStatusException
     */
    public void validateStatusTransition(EStatus newStatus, FocusSession existingSession) throws FocusSessionStatusException {

        EStatus currentStatus = existingSession.getStatus();

        switch (currentStatus) {
            case PENDING -> {
                if (newStatus == EStatus.PENDING) {
                    throw new FocusSessionStatusException(THE_SESSION_STATUS_IS_ALREADY + newStatus);
                }
            }
            case DONE -> throw new FocusSessionStatusException(CANNOT_MARK_DONE_SESSION_AS + newStatus);
            case IN_PROGRESS -> {
                if (newStatus == EStatus.IN_PROGRESS) {
                    throw new FocusSessionStatusException(THE_SESSION_STATUS_IS_ALREADY + newStatus);
                }
            }
            default -> throw new FocusSessionStatusException(UNEXPECTED_SESSION_STATUS + newStatus);
        }
    }
}
