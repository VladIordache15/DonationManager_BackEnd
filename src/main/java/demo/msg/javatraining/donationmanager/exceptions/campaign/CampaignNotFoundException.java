package demo.msg.javatraining.donationmanager.exceptions.campaign;

public class CampaignNotFoundException extends CampaignException {
    private static final String DEFAULT_MESSAGE = "Name is not unique!";

    public CampaignNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public CampaignNotFoundException(String message) {
        super(message);
    }
}
