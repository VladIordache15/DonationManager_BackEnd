package de.msg.javatraining.donationmanager.exceptions.donator;

import de.msg.javatraining.donationmanager.exceptions.donation.DonationException;

public class DonatorNotFoundException extends DonationException {

    private static final String DEFAULT_MESSAGE = "Donor not found!";

    public DonatorNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public DonatorNotFoundException(String message) {
        super(message);
    }
}
