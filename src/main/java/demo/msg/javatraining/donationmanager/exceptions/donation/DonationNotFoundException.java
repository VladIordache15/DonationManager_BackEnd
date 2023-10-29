package demo.msg.javatraining.donationmanager.exceptions.donation;

public class DonationNotFoundException extends DonationException {
    private static final String DEFAULT_MESSAGE = "DonationNotFOundException: Donation not found!";

    public DonationNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public DonationNotFoundException(String message) {
        super(message);
    }
}
