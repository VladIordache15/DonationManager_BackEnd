package de.msg.javatraining.donationmanager.exceptions.donator;

public class DonatorIdException extends DonatorException {
    private static final String DEFAULT_MESSAGE = "Donator id can't be null!";

    public DonatorIdException() {
        super(DEFAULT_MESSAGE);
    }

    public DonatorIdException(String message) {
        super(message);
    }
}
