package de.msg.javatraining.donationmanager.controller.campaign;

import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignIdException;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignNameException;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignRequirementsException;
import de.msg.javatraining.donationmanager.exceptions.user.UserIdException;
import de.msg.javatraining.donationmanager.exceptions.user.UserNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.service.campaignService.CampaignService;
import de.msg.javatraining.donationmanager.service.donationService.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campaign")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private DonationService donationService;


    @GetMapping()
    public List<Campaign> getALlCampaigns(){
        return campaignService.getAllCampaigns();
    }
    @PostMapping("/{userId}") ///campaign/userId
    public ResponseEntity<?> createCapmaign(@PathVariable Long userId,
                                            @RequestBody Campaign campaign) {

        try {
            Campaign camp = campaignService.createCampaign(userId, campaign.getName(), campaign.getPurpose());
            if (camp != null) {
                return ResponseEntity.ok("");
            }
            return ResponseEntity.ok("Donation has not been created!");
        } catch (UserPermissionException
                 | UserNotFoundException
                 | CampaignNameException
                 | CampaignRequirementsException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }



    }

    @PutMapping("/{campId}/{userId}")
    public ResponseEntity<?> updateCampaign(@PathVariable("campId") Long campId,@PathVariable("userId") Long userId, @RequestBody Campaign newCampaign){

        try {
            Campaign camp = campaignService.updateCampaign(userId, campId, newCampaign.getName(), newCampaign.getPurpose());
            if (camp != null) {
                return ResponseEntity.ok("");
            }
            return ResponseEntity.ok("Campaign has not been updated!");
        } catch (UserPermissionException
                | UserNotFoundException
                | CampaignNameException
                | CampaignNotFoundException
                | CampaignRequirementsException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }




    }

    @DeleteMapping("/{campId}/{userId}")
    public ResponseEntity<?> deleteCampaignById(@PathVariable("campId") Long campId,@PathVariable("userId") Long userId) {
        try {
            if (!donationService.findDonationsByCampaignId(campId)) {
                Campaign camp = campaignService.deleteCampaignById(userId, campId);
                if (camp != null) {
                    return ResponseEntity.ok("");
                }
                return ResponseEntity.ok("Campaign can't be deleted!");
            }
            return ResponseEntity.ok("Deletion failed: Campaign has paid Donations!");
        } catch (UserPermissionException
                 | CampaignIdException
                 | CampaignNotFoundException
                 | UserIdException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }

    }
}
