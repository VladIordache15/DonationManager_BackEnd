package de.msg.javatraining.donationmanager.exceptions.donator;

public class DonatorRequirementsException extends DonatorException {
    private static final String DEFAULT_MESSAGE = "Donator requirements not met!";

    public DonatorRequirementsException() {
        super(DEFAULT_MESSAGE);
    }
    public DonatorRequirementsException(String message) {
        super(message);
    }
}
