package demo.msg.javatraining.donationmanager.persistence.notificationSystem;

import demo.msg.javatraining.donationmanager.persistence.model.user.User;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private boolean isRead;
    private boolean hasAppearedOnView;

    private Date createdAt;

    @ManyToOne      // default fetch type = eager
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(     // default fetch type = lazy
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "notification_id")
    private List<NotificationParameter> parameters;

    public Notification() {
    }

    public Notification(NotificationType type, Date createdAt, User user, List<NotificationParameter> parameters) {
        this.type = type;
        this.createdAt = createdAt;
        this.user = user;
        this.parameters = parameters;

        this.isRead = false;
        this.hasAppearedOnView = false;
    }
    public void markAsRead() {
        this.isRead = true;
    }
    public void markAsAppeared() {
        this.hasAppearedOnView = true;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<NotificationParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<NotificationParameter> parameters) {
        this.parameters = parameters;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
