package demo.msg.javatraining.donationmanager.persistence.repository;

import demo.msg.javatraining.donationmanager.persistence.notificationSystem.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("select n from Notification n where n.user.id=:id order by n.createdAt desc")
    List<Notification> getAllNotifications(@Param("id") Long id);

    @Query("select n from Notification n where n.user.id=:id and n.hasAppearedOnView=false order by n.createdAt desc")
    List<Notification> getNotificationsNotAppearedOnView(@Param("id") Long id);

    @Modifying
    @Query("delete from Notification n where n.createdAt<?1")
    void deleteNotificationsBefore(Date date);
}
