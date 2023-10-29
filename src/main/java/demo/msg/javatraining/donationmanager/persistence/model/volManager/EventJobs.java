package demo.msg.javatraining.donationmanager.persistence.model.volManager;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="EventJobs")
@Getter
@Setter
public class EventJobs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jobTitle;
    private String jobDescription;
    private Date jobStartTime;
    private Date jobEndTime;
    private int volsRequired;
    private int volsRegistered;
    private int volsCheckedIn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="event")
    private Event event;
}
