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
@Table(name="SignUps")
@Getter
@Setter
public class SignUps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date checkedInDate;
    private Date subDate;
    private Date startDateJob;
    private Date ednDateJob;
    private boolean checkedIn;
    @ManyToOne
    @JoinColumn(name="volunteer")
    private Volunteer volunteer;

    @ManyToOne
    @JoinColumn(name="eventJob")
    private EventJobs eventJobs;


}
