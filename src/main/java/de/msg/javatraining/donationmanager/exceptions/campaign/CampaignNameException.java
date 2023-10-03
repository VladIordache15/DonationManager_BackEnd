package de.msg.javatraining.donationmanager.exceptions.campaign;

public class CampaignNameException extends CampaignException {
    private static final String DEFAULT_MESSAGE = "Name is not unique!";

    public CampaignNameException() {
        super(DEFAULT_MESSAGE);
    }

    public CampaignNameException(String message) {
        super(message);
    }

}
