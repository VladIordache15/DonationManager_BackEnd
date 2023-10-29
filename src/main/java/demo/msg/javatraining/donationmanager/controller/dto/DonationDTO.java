package demo.msg.javatraining.donationmanager.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationDTO {
    private Long id;
    private float amount;
    private String currency;
    private int idCampaign;
    private int idDonator;
    private int idCreatedBy;
    private Date approveDate;
    private String notes;
    private Date createdDate;
    private int approvedBy;
}
