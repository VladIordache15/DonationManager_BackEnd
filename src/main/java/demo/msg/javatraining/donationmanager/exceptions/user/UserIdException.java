package demo.msg.javatraining.donationmanager.exceptions.user;

public class UserIdException extends UserException {
    private static final String DEFAULT_MESSAGE = "User Id can't be null!";

    public UserIdException() {
        super(DEFAULT_MESSAGE);
    }

    public UserIdException(String message) {
        super(message);
    }
}
