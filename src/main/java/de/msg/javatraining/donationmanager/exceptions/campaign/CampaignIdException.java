package de.msg.javatraining.donationmanager.exceptions.campaign;

public class CampaignIdException extends CampaignException {
    private static final String DEFAULT_MESSAGE = "Campaign Id can't be null!";

    public CampaignIdException() {
        super(DEFAULT_MESSAGE);
    }

    public CampaignIdException(String message) {
        super(message);
    }
}
