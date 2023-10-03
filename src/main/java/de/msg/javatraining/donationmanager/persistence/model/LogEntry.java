package de.msg.javatraining.donationmanager.persistence.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "log_entries")
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    private String level;

    private String message;

    private String changedBy; // This field will store the username of the user who made the change

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    // Constructors, getters, setters, etc.

    public LogEntry() {
    }
}
