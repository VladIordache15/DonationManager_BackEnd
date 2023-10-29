package demo.msg.javatraining.donationmanager.persistence.donationModel;

import demo.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import demo.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import demo.msg.javatraining.donationmanager.persistence.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "donation")
@NoArgsConstructor
@AllArgsConstructor
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // primary key of table

    private float amount;
    private String currency; // maybe switch to another data type? Java has currency class

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campaign")
    private Campaign campaign; //campaignId

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donator")
    private Donor donor; // who donated, donatorId

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "createdBy")
    private User createdBy; // user which created the donation, createdById

    private LocalDate approveDate;
    private String notes;
    private LocalDate createdDate;
    private boolean approved;// = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approvedBy")
    private User approvedBy; // who checked and approved the donation
}
