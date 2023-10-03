package de.msg.javatraining.donationmanager.controller.donation;

import de.msg.javatraining.donationmanager.exceptions.donation.*;
import de.msg.javatraining.donationmanager.exceptions.donator.DonatorRequirementsException;
import de.msg.javatraining.donationmanager.exceptions.user.UserNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.donationModel.Donation;
import de.msg.javatraining.donationmanager.exceptions.donation.DonationNotFoundException;
import de.msg.javatraining.donationmanager.service.donationService.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/donation")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @GetMapping()
    public List<Donation> getAllDonations() {
        return donationService.getAllDonations();
    }

    @GetMapping("/{donationId}")
    public ResponseEntity<?> getDonation(@PathVariable("donationId") Long donationId) {
        try {
            Donation donation = donationService.getDonationById(donationId);
            return ResponseEntity.ok(donation);
        } catch (DonationNotFoundException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }
    }

    @GetMapping("/camp/{campaignId}")
    public List<Donation> getDonationsByCampaignId(@PathVariable("campaignId") Long campaignId){
        return donationService.getDonationsByCampaignId(campaignId);
    }

    @PostMapping("/{donatorId}/{campaignId}/{userId}")
    public ResponseEntity<?> createDonation(@PathVariable("userId") Long userId,
                                            @PathVariable("donatorId") Long donatorId,
                                            @PathVariable("campaignId") Long campaignId,
                                            @RequestBody Donation donation) {
        ResponseEntity<?> response;
        try {
            Donation don = donationService.createDonation(userId, donatorId, campaignId, donation);
            response = new ResponseEntity<>(don, HttpStatusCode.valueOf(200));
        } catch (UserPermissionException | DonationRequirementsException | DonationException exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

    @PutMapping("/{donationId}/{userId}")
    public ResponseEntity<?> updateDonation(@PathVariable("userId") Long userId,
                                            @PathVariable("donationId") Long donationId,
                                            @RequestBody Donation newDonation) {

        ResponseEntity<?> response;
        try {
            Donation don = donationService.updateDonation(userId, donationId, newDonation);
            response = new ResponseEntity<>(don, HttpStatusCode.valueOf(200));
        } catch (DonationRequirementsException
                 | DonationIdException
                 | UserPermissionException
                 | DonationNotFoundException
                 | DonationApprovedException exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;

    }

    @PatchMapping("/{donationId}/{userId}")
    public ResponseEntity<?> approveDonation(@PathVariable("donationId") Long donationId,
                                             @PathVariable("userId") Long userId) {

        ResponseEntity<?> response;
        try {
            Donation donation = donationService.getDonationById(donationId);
            donationService.approveDonation(donationId, userId);
            response = new ResponseEntity<>(donation, HttpStatusCode.valueOf(200));
        } catch (DonationNotFoundException | UserNotFoundException | DonationApprovedException | DonationUserException exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

    @DeleteMapping("/{donationId}/{userId}")
    public ResponseEntity<?> deleteDonationById(@PathVariable("userId") Long userId,
                                                @PathVariable("donationId") Long donationId) {

        ResponseEntity<?> response;
        try {
            Donation donation = donationService.getDonationById(donationId);
            donationService.deleteDonationById(userId, donationId);
            response = new ResponseEntity<>(donation, HttpStatusCode.valueOf(200));
        } catch (DonationIdException
                 | DonationNotFoundException
                 | DonationApprovedException
                 | UserPermissionException exception) {
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));

        }
        return response;
    }

}
