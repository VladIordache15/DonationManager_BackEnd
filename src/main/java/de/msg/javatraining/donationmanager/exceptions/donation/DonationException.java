package de.msg.javatraining.donationmanager.exceptions.donation;

public class DonationException extends Exception {
    private static final String DEFAULT_MESSAGE = "Eroare din DonationException class!";

    public DonationException() {
        super(DEFAULT_MESSAGE);
    }

    public DonationException(String message) {
        super(message);
    }
}

