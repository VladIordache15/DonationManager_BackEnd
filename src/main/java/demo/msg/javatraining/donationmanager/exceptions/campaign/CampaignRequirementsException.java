package demo.msg.javatraining.donationmanager.exceptions.campaign;

public class CampaignRequirementsException extends CampaignException {
    private static final String DEFAULT_MESSAGE = "Name or purpose can't be null!";

    public CampaignRequirementsException() {
        super(DEFAULT_MESSAGE);
    }

    public CampaignRequirementsException(String message) {
        super(message);
    }
}
