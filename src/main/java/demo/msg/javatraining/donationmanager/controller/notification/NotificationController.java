package demo.msg.javatraining.donationmanager.controller.notification;

import demo.msg.javatraining.donationmanager.persistence.notificationSystem.Notification;
import demo.msg.javatraining.donationmanager.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api")
public class NotificationController {
    private ExecutorService executor = Executors.newFixedThreadPool(5);

    @Autowired
    NotificationService notificationService;

    @GetMapping("/notify/{userId}")
    public DeferredResult<List<NotificationDTO>> publisher(@PathVariable Long userId) {
        DeferredResult<List<NotificationDTO>> output = new DeferredResult<>(5000L);

        executor.execute(() -> {
            try {
                List<Notification> notifications = notificationService.getNotificationsNotAppearedOnView(userId);
                notifications.stream()
                        .map(Notification::getId)
                        .forEach(id -> notificationService.markNotificationAsAppeared(id));

                List<NotificationDTO> dtos = NotificationConverter.convertToDTOs(notifications);

                output.onTimeout(() -> output.setErrorResult("The service is not responding in allowed time!"));
                output.setResult(dtos);
            } catch (Exception e) {
                output.setErrorResult("Something went wrong with your service!");
            }
        });

        return output;
    }
    @PutMapping("/notifications/{id}")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/inbox/{userId}")
    public DeferredResult<List<NotificationDTO>> getAllNotifications(@PathVariable Long userId) {
        DeferredResult<List<NotificationDTO>> output = new DeferredResult<>(5000L);

        executor.execute(() -> {
            try {
                List<Notification> notifications = notificationService.getAllNotifications(userId);
                List<NotificationDTO> dtos = NotificationConverter.convertToDTOs(notifications);

                output.onTimeout(() -> output.setErrorResult("The service is not responding in allowed time!"));
                output.setResult(dtos);
            } catch (Exception e) {
                output.setErrorResult("Something went wrong with your service!");
            }
        });

        return output;
    }

}
