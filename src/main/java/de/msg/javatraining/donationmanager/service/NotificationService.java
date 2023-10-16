package de.msg.javatraining.donationmanager.service;

import de.msg.javatraining.donationmanager.controller.notification.NotificationConverter;
import de.msg.javatraining.donationmanager.controller.notification.NotificationDTO;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.Notification;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationParameter;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationType;
import de.msg.javatraining.donationmanager.persistence.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private LogService logService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;



    public void saveNotification(User user, List<NotificationParameter> parameters, NotificationType type) {
            Notification notification = new Notification(type, new Date(), user, parameters);
            notificationRepository.save(notification);
         List<NotificationDTO> listaDTO = NotificationConverter.convertToDTOs(notificationRepository.getAllNotifications(user.getId()));
        simpMessagingTemplate.convertAndSend("/topic/data.response", listaDTO);

        List<Notification> notifications = this.getNotificationsNotAppearedOnView(user.getId());
        notifications.stream()
                .map(Notification::getId)
                .forEach(this::markNotificationAsAppeared);
        List<NotificationDTO> listaDTO2 = NotificationConverter.convertToDTOs(notifications);

        simpMessagingTemplate.convertAndSend("/topic/data.publish", listaDTO2);



    }



    public void markNotificationAsRead(Long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new RuntimeException("Notification not found"));
            notification.markAsRead();
            notificationRepository.save(notification);
//            logService.logOperation("UPDATE", "Marked notification as read: " + notificationId, null);
        } catch (Exception e) {
//            logService.logOperation("ERROR", "Error marking notification as read: " + notificationId + ". " + e.getMessage(), null);
        }
    }

    public void markNotificationAsAppeared(Long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new RuntimeException("Notification not found"));
            notification.markAsAppeared();
            notificationRepository.save(notification);
//            logService.logOperation("UPDATE", "Marked notification as appeared: " + notificationId, null);
        } catch (Exception e) {
//            logService.logOperation("ERROR", "Error marking notification as appeared: " + notificationId + ". " + e.getMessage(), null);
        }
    }


    @Scheduled(cron = "0 */1 * * * ?")  // Every minute for testing
    public void deleteRecentNotifications() {
        Date fewMinutesAgo = getFewMinutesAgo();
        System.out.println("Running deleteRecentNotifications at: " + new Date());
        System.out.println("Deleting notifications before: " + fewMinutesAgo);
        notificationRepository.deleteNotificationsBefore(fewMinutesAgo);
    }

    private Date getFewMinutesAgo() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -5); // Let's say 5 minutes for this example
        return cal.getTime();
    }

    public List<Notification> getNotificationsNotAppearedOnView(Long userId) {
        try {
            List<Notification> notifications = notificationRepository.getNotificationsNotAppearedOnView(userId);
            System.out.println(notifications);
            // No need to log a fetch operation, but you can if you want.
            return notifications;
        } catch (Exception e) {
            throw e; // Re-throwing the exception after logging it.
        }
    }

    public List<Notification> getAllNotifications(Long userId) {

            // No need to log a fetch operation, but you can if you want
            return notificationRepository.getAllNotifications(userId);

    }
}
