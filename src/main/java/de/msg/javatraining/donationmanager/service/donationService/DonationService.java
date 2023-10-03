package de.msg.javatraining.donationmanager.service.donationService;

import de.msg.javatraining.donationmanager.exceptions.donation.DonationApprovedException;
import de.msg.javatraining.donationmanager.exceptions.donation.DonationIdException;
import de.msg.javatraining.donationmanager.exceptions.donation.DonationNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.donation.DonationRequirementsException;
import de.msg.javatraining.donationmanager.exceptions.donation.*;
import de.msg.javatraining.donationmanager.exceptions.user.UserNotFoundException;
import de.msg.javatraining.donationmanager.persistence.donationModel.*;
import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationParameter;
import de.msg.javatraining.donationmanager.persistence.notificationSystem.NotificationType;
import de.msg.javatraining.donationmanager.persistence.repository.CampaignRepository;
import de.msg.javatraining.donationmanager.persistence.repository.DonationRepository;
import de.msg.javatraining.donationmanager.persistence.repository.DonorRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.LogService;
import de.msg.javatraining.donationmanager.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LogService logService;

    private final PermissionEnum permission = PermissionEnum.DONATION_MANAGEMENT;

    private boolean checkDonationRequirements(Donation donation) {
        return donation.getAmount() >= 0
                && donation.getCurrency() != null
                && donation.getCampaign() != null
                && donation.getCreatedBy() != null
                && donation.getDonor() != null;
    }

    private boolean checkUserPermission(Long userId, PermissionEnum requiredPermission) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            for (Role role : user.getRoles()) {
                if (role.getPermissions().contains(requiredPermission)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkExistance(Long donatorId, Long campaignId) {
        Optional<Donor> donator = donorRepository.findById(donatorId);
        Optional<Campaign> campaign = campaignRepository.findById(campaignId);

        return donator.isPresent() && campaign.isPresent();
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    public Donation getDonationById(Long id) throws DonationNotFoundException {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(DonationNotFoundException::new);
        return donation;
    }

    public Donation approveDonation(Long donationId, Long userId) throws DonationNotFoundException, UserNotFoundException, DonationApprovedException, DonationUserException {
        User user = userRepository.findById(userId).get();
        Donation donation = donationRepository.findById(donationId).get();

        if (userRepository.findById(userId).isEmpty()) {
            logService.logOperation("ERROR", "User not found!", user.getUsername());
            throw new UserNotFoundException();
        }
        if (donationRepository.findById(donationId).isEmpty()) {
            logService.logOperation("ERROR", "Donation not found!", user.getUsername());
            throw new DonationNotFoundException();
        }

        if (donation.isApproved()) {
            logService.logOperation("ERROR", "Donation has already been approved! Can't delete an approved Donation!", user.getUsername());
            throw new DonationApprovedException();
        }

        if (Objects.equals(donation.getCreatedBy().getId(), userId)) {
            logService.logOperation("ERROR", "Donation needs to be approved by a different user than the one who created it!", user.getUsername());
            throw new DonationUserException();
        }

        donation.setApprovedBy(user);
        donation.setApproved(true);
        donation.setApproveDate(LocalDate.now());
        donationRepository.save(donation);

        List<NotificationParameter> parameters = new ArrayList<>(Arrays.asList(
                new NotificationParameter(String.valueOf(donation.getAmount()))
        ));
        notificationService.saveNotification(user, parameters, NotificationType.DONATION_APPROVED);

        logService.logOperation("UPDATE", "Donation approved", user.getUsername());
        return donation;
    }

    public Donation createDonation(Long userId, Long donatorId, Long campaignId, Donation donation) throws
            DonationRequirementsException,
            UserPermissionException,
            DonationException {
        Optional<User> user = userRepository.findById(userId);

        if (!checkDonationRequirements(donation)) {
            logService.logOperation("ERROR", "Donation requirements not met!", user.get().getUsername());
            throw new DonationRequirementsException();
        }

        if (!checkUserPermission(userId, permission)) {
            throw new UserPermissionException();
        }

        if (!checkExistance(donatorId, campaignId)) {
            throw new DonationException("Problem with DonatorId or CampaignId");
        }


        Optional<Donor> donator = donorRepository.findById(donatorId);
        Optional<Campaign> campaign = campaignRepository.findById(campaignId);

        if (user.isPresent() && donator.isPresent() && campaign.isPresent()) {
            donation.setCreatedBy(user.get());
            donation.setDonor(donator.get());
            donation.setCampaign(campaign.get());
            donation.setCreatedDate(LocalDate.now());
            donation.setApproved(false);
            donationRepository.save(donation);
            logService.logOperation("INSERT", "Created donation", user.get().getUsername());
            return donation;
        } else {
            logService.logOperation("ERROR", "User and/or donator and/or campaign not present!", user.get().getUsername());
            throw new DonationException("User and/or donator and/or campaign not present!");
        }
    }

    public Donation deleteDonationById(Long userId, Long donationId) throws
            DonationIdException,
            DonationNotFoundException,
            DonationApprovedException,
            UserPermissionException {
        Optional<User> user = userRepository.findById(userId);

        if (donationId == null) {
            logService.logOperation("ERROR", "Id can't be null!", user.get().getUsername());
            throw new DonationIdException();
        }

        Optional<Donation> donation = donationRepository.findById(donationId);
                //.orElseThrow(DonationNotFoundException::new);


        if (donation.isEmpty()) {
            logService.logOperation("ERROR", "DonationNotFOundException: Donation not found!", user.get().getUsername());
            throw new DonationNotFoundException();
        }

        if (donation.get().isApproved()) {
            logService.logOperation("ERROR", "Donation has already been approved! Can't delete an approved Donation!", user.get().getUsername());
            throw new DonationApprovedException();
        }

        if (!checkUserPermission(userId, permission)) {
            logService.logOperation("ERROR", "User does not have the required permission/s!", user.get().getUsername());
            throw new UserPermissionException();
        }



        donationRepository.deleteById(donationId);
        logService.logOperation("DELETE", "Deleted donation", user.get().getUsername());

        return donation.get();
    }

    public Donation updateDonation(Long userId, Long donationId, Donation updatedDonation) throws
            DonationIdException,
            DonationRequirementsException,
            UserPermissionException,
            DonationNotFoundException,
            DonationApprovedException {

        Optional<User> user = userRepository.findById(userId);

        // Check if donationId is null
        if (donationId == null) {
            logService.logOperation("ERROR", "Id can't be null!", user.get().getUsername());
            throw new DonationIdException();
        }

        // Check if the updated donation meets the requirements
        if (!checkDonationRequirements(updatedDonation)) {
            logService.logOperation("ERROR", "Donation requirements not met!", user.get().getUsername());
            throw new DonationRequirementsException();
        }

        // Check user permission
        if (!checkUserPermission(userId, permission)) {
            logService.logOperation("ERROR", "User does not have the required permission/s!", user.get().getUsername());
            throw new UserPermissionException();
        }

        // Find the donation by ID
        Optional<Donation> donation = donationRepository.findById(donationId);

        // Check if the donation exists
        if (donation.isEmpty()) {
            logService.logOperation("ERROR", "DonationNotFOundException: Donation not found!", user.get().getUsername());
            throw new DonationNotFoundException();
        }

        // Check if the donation is not approved
        if (donation.get().isApproved()) {
            logService.logOperation("ERROR", "Donation has already been approved! Can't delete an approved Donation!", user.get().getUsername());
            throw new DonationApprovedException();
        }

        // Update the donation fields
        Donation existingDonation = donation.get();
        if (updatedDonation.getAmount() != 0) {
            existingDonation.setAmount(updatedDonation.getAmount());
        }
        if (updatedDonation.getCurrency() != null) {
            existingDonation.setCurrency(updatedDonation.getCurrency());
        }
        if (updatedDonation.getCampaign() != null) {
            existingDonation.setCampaign(updatedDonation.getCampaign());
        }
        if (updatedDonation.getDonor() != null) {
            existingDonation.setDonor(updatedDonation.getDonor());
        }
        if (updatedDonation.getNotes() != null) {
            existingDonation.setNotes(updatedDonation.getNotes());
        }

        // Save the updated donation
        donationRepository.save(existingDonation);
        logService.logOperation("UPDATE", "Updated donation", user.get().getUsername());

        return existingDonation;
    }


    public boolean findDonationsByDonatorId(Long donatorId) {
        try {
            List<Donation> donations = donationRepository.findByDonorId(donatorId);
            return donations.size() > 0;
        } catch (IllegalStateException exception) {
            System.out.println("Donator doesn't have donations or doesn't exist");
        }
        return false;
    }

    public boolean findDonationsByCampaignId(Long id){
        int counter=0;
        try {
            List<Donation> donations = donationRepository.findDonationsByCampaignId(id);
            for(Donation d : donations){
                if(d.getAmount()!=0)
                    counter++;

            }

            return counter != 0;

        }catch (Exception e){
            System.out.println("problema la donatie");
            return false;
        }

    }

    public List<Donation> getDonationsByCampaignId(Long id){
        return donationRepository.findDonationsByCampaignId(id);
    }
}
