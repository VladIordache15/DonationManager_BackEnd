package demo.msg.javatraining.donationmanager.exceptions.user;

public class UserPermissionException extends UserException {
    private static final String DEFAULT_MESSAGE = "User does not have the required permission/s!";

    public UserPermissionException() {
        super(DEFAULT_MESSAGE);
    }

    public UserPermissionException(String message) {
        super(message);
    }
}
