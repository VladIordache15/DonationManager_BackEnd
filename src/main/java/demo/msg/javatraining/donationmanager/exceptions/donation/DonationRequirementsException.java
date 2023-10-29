package demo.msg.javatraining.donationmanager.exceptions.donation;

public class DonationRequirementsException extends Exception {
    private static final String DEFAULT_MESSAGE = "Donation requirements not met!";

    public DonationRequirementsException() {
        super(DEFAULT_MESSAGE);
    }

    public DonationRequirementsException(String message) {
        super(message);
    }
}
