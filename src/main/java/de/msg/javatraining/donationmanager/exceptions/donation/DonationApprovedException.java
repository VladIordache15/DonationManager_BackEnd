package de.msg.javatraining.donationmanager.exceptions.donation;

public class DonationApprovedException extends DonationException {
    private static final String DEFAULT_MESSAGE = "Donation has already been approved! Can't delete an approved Donation!";

    public DonationApprovedException() {
        super(DEFAULT_MESSAGE);
    }
    public DonationApprovedException(String message) {
        super(message);
    }
}

