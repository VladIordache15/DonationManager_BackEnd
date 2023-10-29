package demo.msg.javatraining.donationmanager.controller.notification;

import demo.msg.javatraining.donationmanager.persistence.notificationSystem.Notification;
import demo.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationParameter;

import java.util.ArrayList;
import java.util.List;

public class NotificationConverter {

    public static List<NotificationDTO> convertToDTOs(List<Notification> notifications) {
        List<NotificationDTO> notificationDTOs = new ArrayList<>();

        for (Notification notification : notifications) {
            String idString = notification.getId().toString();
            String typeString = notification.getType().name();
            List<String> parameterStrings = new ArrayList<>();
            boolean isRead = notification.isRead();

            for (NotificationParameter parameter : notification.getParameters()) {
                parameterStrings.add(parameter.getValue()); // Assuming NotificationParameter has a getValue() method returning a string
            }

            NotificationDTO dto = new NotificationDTO(idString, typeString, parameterStrings, isRead);
            notificationDTOs.add(dto);
        }

        return notificationDTOs;
    }
}
