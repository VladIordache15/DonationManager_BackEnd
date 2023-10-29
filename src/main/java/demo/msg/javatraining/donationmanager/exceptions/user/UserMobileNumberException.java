package demo.msg.javatraining.donationmanager.exceptions.user;

public class UserMobileNumberException extends UserException {

    private static final String DEFAULT_MESSAGE = "Mobile number in use or not valid!";

    public UserMobileNumberException() {
        super(DEFAULT_MESSAGE);
    }

    public UserMobileNumberException(String message) {
        super(message);
    }
}
