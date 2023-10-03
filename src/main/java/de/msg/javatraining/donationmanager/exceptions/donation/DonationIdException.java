package de.msg.javatraining.donationmanager.exceptions.donation;

public class DonationIdException extends DonationException {

    private static final String DEFAULT_MESSAGE = "Id can't be null!";

    public DonationIdException() {
        super(DEFAULT_MESSAGE);
    }

    public DonationIdException(String message) {
        super(message);
    }
}
