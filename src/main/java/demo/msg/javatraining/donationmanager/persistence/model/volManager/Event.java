package demo.msg.javatraining.donationmanager.persistence.model.volManager;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@Table(name="Event")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Event implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventName;
    private Date eventStartDate;
    private Date eventEndDate;
    private String description;
    private int openJobs;
    private int volsRequired;
    private int volsRegistered;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status eventStatus;
    private String notes;

//    @OneToMany(fetch = FetchType.EAGER)
//    @JoinColumn(name="eventJobs")
//    private Set<EventJobs> eventJobs;






}
