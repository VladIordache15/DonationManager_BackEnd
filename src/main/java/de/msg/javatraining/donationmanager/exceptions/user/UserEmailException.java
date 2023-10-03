package de.msg.javatraining.donationmanager.exceptions.user;

public class UserEmailException extends UserException {
    private static final String DEFAULT_MESSAGE = "Email in use or not valid!";

    public UserEmailException() {
        super(DEFAULT_MESSAGE);
    }

    public UserEmailException(String message) {
        super(message);
    }
}
