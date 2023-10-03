package de.msg.javatraining.donationmanager.persistence.notificationSystem;

public enum NotificationType {
    WELCOME_NEW_USER(1),
    USER_UPDATED(2),
    USER_DEACTIVATED_MANUAL(3),
    USER_DEACTIVATED_INCORRECT_PASSWORD(4),
    DONATION_APPROVED(5);

    private final int type;

    public int getType() {
        return type;
    }

    NotificationType(int type) {
        this.type = type;
    }
}
