package de.msg.javatraining.donationmanager.persistence.notificationSystem;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parameter")
public class NotificationParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;

    public NotificationParameter() {
    }

    public NotificationParameter(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public NotificationParameter(NotificationParameter other) {
        this.value = other.value;
    }

    public static List<NotificationParameter> deepCopyList(List<NotificationParameter> originalList) {
        List<NotificationParameter> copiedList = new ArrayList<>();
        for (NotificationParameter param : originalList) {
            copiedList.add(new NotificationParameter(param));
        }
        return copiedList;
    }

}
