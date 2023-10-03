package de.msg.javatraining.donationmanager.exceptions.donation;

public class DonationUserException extends DonationException {
    private final static String DEFAULT_MESSAGE = "Donation needs to be approved by a different user than the one who created it!";

    public DonationUserException() {
        super(DEFAULT_MESSAGE);
    }
}
