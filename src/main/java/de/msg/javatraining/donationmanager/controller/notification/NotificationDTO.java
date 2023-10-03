package de.msg.javatraining.donationmanager.controller.notification;

import java.util.List;

public class NotificationDTO {

    private String id;
    private String type;
    private List<String> parameters;
    private boolean isRead;

    public NotificationDTO() {
    }

    public NotificationDTO(String id, String type, List<String> parameters, boolean isRead) {
        this.id = id;
        this.type = type;
        this.parameters = parameters;
        this.isRead = isRead;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}
