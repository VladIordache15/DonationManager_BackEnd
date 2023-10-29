package demo.msg.javatraining.donationmanager.controller.donor;

import demo.msg.javatraining.donationmanager.exceptions.donator.DonatorIdException;
import demo.msg.javatraining.donationmanager.exceptions.donator.DonatorNotFoundException;
import demo.msg.javatraining.donationmanager.exceptions.donator.DonatorRequirementsException;
import demo.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import demo.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import demo.msg.javatraining.donationmanager.service.donationService.DonationService;
import demo.msg.javatraining.donationmanager.service.donorService.DonorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donator")
public class DonorController {
    @Autowired
    private DonorService donorService;

    @Autowired
    private DonationService donationService;

    @GetMapping()
    public List<Donor> getAllDonators() {
        return donorService.getAllDonators();
    }

    @GetMapping("/camp/{campaignId}")
    public List<Donor> getDonatorsByCampaignId(@PathVariable("campaignId") Long campaignId){
        return donorService.getDonatorsByCampaignId(campaignId);
    }

    @GetMapping("/{donatorId}")
    public ResponseEntity<?> getDonator(@PathVariable("donatorId") Long donatorId) {
        try {
            Donor donor = donorService.getDonatorById(donatorId);
            return ResponseEntity.ok(donor);
        } catch (DonatorNotFoundException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> createDonator(@PathVariable("userId") Long userId, @RequestBody Donor donor) {
        ResponseEntity<?> response;
        try {
            Donor don = donorService.createDonator(userId, donor);
            response = new ResponseEntity<>(don, HttpStatusCode.valueOf(200));
        } catch (UserPermissionException | DonatorRequirementsException exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

    @PutMapping("/{donatorId}/{userId}")
    public ResponseEntity<?> updateDonator(@PathVariable("userId") Long userId,
                                           @PathVariable("donatorId") Long donatorId,
                                           @RequestBody Donor newDonor) {
        ResponseEntity<?> response;
        try {
            Donor don = donorService.updateDonator(userId, donatorId, newDonor);
            response = new ResponseEntity<>(don, HttpStatusCode.valueOf(200));

        } catch (DonatorIdException
                 | UserPermissionException
                 | DonatorRequirementsException
                 | DonatorNotFoundException exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

    @DeleteMapping("/{donatorId}/{userId}")
    public ResponseEntity<?> deleteDonatorById(@PathVariable("userId") Long userId, @PathVariable("donatorId") Long donatorId) {
        ResponseEntity<?> response;
        try {
            if (donationService.findDonationsByDonatorId(donatorId)) {
                Donor updateValues = new Donor("UNKNOWN", "UNKNOWN", "UNKNOWN","UNKNOWN");
                Donor don = donorService.updateDonator(userId, donatorId, updateValues);
                response = new ResponseEntity<>(don, HttpStatusCode.valueOf(200));
            } else {
                Donor don =donorService.deleteDonatorById(userId, donatorId);
                response = new ResponseEntity<>(don, HttpStatusCode.valueOf(200));
            }
        } catch (DonatorNotFoundException
                 | DonatorIdException
                 | UserPermissionException
                 | DonatorRequirementsException exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

}
