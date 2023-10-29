package demo.msg.javatraining.donationmanager.service.donorService;

import demo.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import demo.msg.javatraining.donationmanager.exceptions.donator.DonatorIdException;
import demo.msg.javatraining.donationmanager.exceptions.donator.DonatorNotFoundException;
import demo.msg.javatraining.donationmanager.exceptions.donator.DonatorRequirementsException;
import demo.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import demo.msg.javatraining.donationmanager.persistence.model.Role;
import demo.msg.javatraining.donationmanager.persistence.model.user.User;
import demo.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import demo.msg.javatraining.donationmanager.persistence.repository.DonorRepository;
import demo.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import demo.msg.javatraining.donationmanager.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonorService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private LogService logService;

    private final PermissionEnum permission = PermissionEnum.BENEF_MANAGEMENT;

    private boolean checkDonatorRequirements(Donor donor) {
        return donor.getFirstName() != null && donor.getLastName() != null;
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


    public List<Donor> getAllDonators() {
        return donorRepository.findAll();
    }

    public List<Donor> getDonatorsByCampaignId(Long id){
        return donorRepository.findDonatorsByCampaignId(id);
    }

    public Donor getDonatorById(Long id) throws DonatorNotFoundException {
        Donor donor = donorRepository.findById(id)
                .orElseThrow(DonatorNotFoundException::new);
        return donor;
    }

    public Donor createDonator(Long userId, Donor donor) throws
            UserPermissionException,
            DonatorRequirementsException {
        Optional<User> user = userRepository.findById(userId);
        if (checkDonatorRequirements(donor)) {
            if (checkUserPermission(userId, permission)) {
                donorRepository.save(donor);
                logService.logOperation("INSERT", "Created donator", user.get().getUsername());
                return donor;
            } else {
                logService.logOperation("ERROR", "Donator id can't be null!", user.get().getUsername());
                throw new UserPermissionException("User does not have the required permission/s!");
            }
        } else {
            logService.logOperation("ERROR", "Donator does not meet the requirements!", user.get().getUsername());
            throw new DonatorRequirementsException();
        }
    }

    public Donor deleteDonatorById(Long userId, Long donatorId) throws
            DonatorIdException,
            DonatorNotFoundException,
            UserPermissionException {

        Optional<User> user = userRepository.findById(userId);

        if (donatorId == null) {
            logService.logOperation("ERROR", "Donator id can't be null!", user.get().getUsername());
            throw new DonatorIdException();
        }


        Donor donor = donorRepository.findById(donatorId)
                .orElseThrow(DonatorNotFoundException::new);

        if (checkUserPermission(userId, permission)) {
            donorRepository.deleteById(donatorId);
            logService.logOperation("DELETE", "Deleted donor", user.get().getUsername());
            return donor;
        } else {
            logService.logOperation("ERROR", "User does not have the required permission/s!", user.get().getUsername());
            throw new UserPermissionException();
        }
    }

    public Donor updateDonator(Long userId, Long donatorId, Donor updatedDonor) throws
            DonatorIdException,
            UserPermissionException,
            DonatorRequirementsException,
            DonatorNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (donatorId == null) {
            logService.logOperation("ERROR", "Donator id can't be null!", user.get().getUsername());
            throw new DonatorIdException();
        }

        if (!checkUserPermission(userId, permission)) {
            logService.logOperation("ERROR", "User does not have the required permission/s!", user.get().getUsername());
            throw new UserPermissionException();
        }

        if (!checkDonatorRequirements(updatedDonor)) {
            logService.logOperation("ERROR", "Donator requirements not met!", user.get().getUsername());
            throw new DonatorRequirementsException();
        }

        Donor donor = donorRepository.findById(donatorId)
                .orElseThrow(DonatorNotFoundException::new);

        if (updatedDonor.getAdditionalName() != null) {
            donor.setAdditionalName(updatedDonor.getAdditionalName());
        }
        if (updatedDonor.getFirstName() != null) {
            donor.setFirstName(updatedDonor.getFirstName());
        }
        if (updatedDonor.getLastName() != null) {
            donor.setLastName(updatedDonor.getLastName());
        }
        if (updatedDonor.getMaidenName() != null) {
            donor.setMaidenName(updatedDonor.getMaidenName());
        }
        donorRepository.save(donor);
        logService.logOperation("UPDATE", "Updated donation", user.get().getUsername());
        return donor;
    }

}
