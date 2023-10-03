package de.msg.javatraining.donationmanager.service.campaignService;


import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignIdException;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignNameException;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignRequirementsException;
import de.msg.javatraining.donationmanager.exceptions.donation.DonationNotFoundException;
import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.donationModel.Donation;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.exceptions.user.UserIdException;
import de.msg.javatraining.donationmanager.exceptions.user.UserNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.repository.CampaignRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CampaignService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private LogService logService;


    private final PermissionEnum permission = PermissionEnum.CAMP_MANAGEMENT;

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

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    public Campaign createCampaign(Long userId, String name, String purpose) throws
            CampaignRequirementsException,
            CampaignNameException,
            UserPermissionException,
            UserNotFoundException {

        if (name == null || purpose == null) {
            throw new CampaignRequirementsException();
        }

        Optional<User> userADMIN = userRepository.findById(userId);

        if (userADMIN.isPresent()) {
            PermissionEnum adminPermissionToCheck = PermissionEnum.CAMP_MANAGEMENT;
            boolean hasAdminPermission = false;

            for (Role adminRole : userADMIN.get().getRoles()) {
                if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
                    hasAdminPermission = true;
                    break;
                }
            }

            if (hasAdminPermission) {
                if (campaignRepository.findCampaignByName(name) != null) {
                    throw new CampaignNameException();
                } else {
                    Campaign campaign = new Campaign(name, purpose);
                    campaignRepository.save(campaign);
                    logService.logOperation("INSERT", "added campaign with id:" + campaign.getId(), userADMIN.get().getUsername());
                    return campaign;
                }
            } else {
                logService.logOperation("ERROR", "User does not have the required permission", userADMIN.get().getUsername());

                throw new UserPermissionException();

            }
        } else {
            logService.logOperation("ERROR", "User not found!", null);

            throw new UserNotFoundException();
        }

    }


    public Campaign updateCampaign(Long userId, Long campaignId, String name, String purpose) throws
            CampaignRequirementsException,
            CampaignNameException,
            CampaignNotFoundException,
            UserPermissionException,
            UserNotFoundException {

        if (name == null || purpose == null) {
            throw new CampaignRequirementsException();
        }

        Optional<User> userADMIN = userRepository.findById(userId);

        if (userADMIN.isPresent()) {
            PermissionEnum adminPermissionToCheck = PermissionEnum.CAMP_MANAGEMENT;
            boolean hasAdminPermission = false;

            for (Role adminRole : userADMIN.get().getRoles()) {
                if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
                    hasAdminPermission = true;
                    break;
                }
            }

            if (hasAdminPermission) {
                Optional<Campaign> campaignOptional = campaignRepository.findById(campaignId);

                if (campaignOptional.isPresent()) {
                    Campaign campaign = campaignOptional.get();

                    if (!campaign.getName().equals(name)) {
                        // Check for uniqueness of the new name
                        if (campaignRepository.findCampaignByName(name) != null) {
                            throw new CampaignNameException();
                        }
                    }

                    campaign.setName(name);
                    campaign.setPurpose(purpose);
                    campaignRepository.save(campaign);
                    logService.logOperation("UPDATE", "updated campaign with id:" + campaign.getId(), userADMIN.get().getUsername());


                    return campaign;
                } else {
                    logService.logOperation("ERROR", "Name is not unique!", userADMIN.get().getUsername());

                    throw new CampaignNotFoundException();
                }
            } else {
                logService.logOperation("ERROR", "User does not have the required permission", userADMIN.get().getUsername());

                throw new UserPermissionException();
            }
        } else {
            logService.logOperation("ERROR", "User not found!", null);

            throw new UserNotFoundException();
        }

    }


    /**
     * Deletes a campaign by its ID; if the campaign has paid donations deletion fails .
     *
     * @param userId     The ID of the user requesting the deletion.
     * @param campaignId The ID of the campaign to be deleted.
     * @return The deleted campaign.
     * @throws UserIdException         If the provided user ID is null.
     * @throws CampaignIdException     If the provided campaign ID is null.
     * @throws UserPermissionException If the user does not have the necessary permission for deletion.
     */
    public Campaign deleteCampaignById(Long userId, Long campaignId) throws
            UserIdException,
            CampaignIdException,
            CampaignNotFoundException,
            UserPermissionException {

        if (userId == null) {

            throw new UserIdException();
        }

        if (campaignId == null) {
            throw new CampaignIdException();
        }

        if (!checkUserPermission(userId, permission)) {

            throw new UserPermissionException();
        }

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        Campaign toDeleteCampaign = campaignRepository.findCampaignById(campaignId);
        List<User> users = userRepository.findAll();
        for (User user : users) {
            Set<Campaign> userCampaigns = user.getCampaigns();
            if (userCampaigns.contains(toDeleteCampaign)) {
                userCampaigns.remove(toDeleteCampaign);
                userRepository.save(user); // Update the user entity
            }
        }
        campaignRepository.deleteById(campaignId);
        logService.logOperation("DELETE","deleted campaign with id:"+campaignId, userRepository.findById(userId).get().getUsername());

        return campaign;

    }


}
