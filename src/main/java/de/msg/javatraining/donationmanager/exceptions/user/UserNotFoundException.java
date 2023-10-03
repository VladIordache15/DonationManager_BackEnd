package de.msg.javatraining.donationmanager.exceptions.user;

public class UserNotFoundException extends UserException {
    private static final String DEFAULT_MESSAGE = "User not found!";

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
