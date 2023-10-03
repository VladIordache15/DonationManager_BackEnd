package campaignTests;

import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignNameException;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignRequirementsException;
import de.msg.javatraining.donationmanager.exceptions.user.UserNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.repository.CampaignRepository;
import de.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.LogService;
import de.msg.javatraining.donationmanager.service.campaignService.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceTest {
    @InjectMocks
    private CampaignService campaignService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private LogService logservice;

    @Mock
    private CampaignRepository campaignRepository;

    private User createUserWithRoleAndPermission1(Long userId,PermissionEnum perm) {
        Set<PermissionEnum> permissionEnums = new HashSet<>();

        permissionEnums.add(perm);
        Set<Role> roles = new HashSet<>();
        Role role = new Role(1, ERole.ROLE_ADM, permissionEnums);
        roles.add(role);

        User user = new User(userId,"testuser", "test", "1234567890", "something", "test@example.com", "psswd", true, false, 1, roles, new HashSet<>());
        return user;
    }
    @Test
    public void testCreateCampaignWithValidPermissionsAndUniqueName() throws UserPermissionException, UserNotFoundException, CampaignNameException, CampaignRequirementsException {


        User user = createUserWithRoleAndPermission1(1L,PermissionEnum.CAMP_MANAGEMENT);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // Prepare mock campaign repository
        // Simulating unique name
        when(campaignRepository.findCampaignByName(anyString())).thenReturn(null);

        Campaign createdCampaign = campaignService.createCampaign(1L, "New Campaign", "Purpose");

        // Verify
        assertNotNull(createdCampaign);
        assertEquals("New Campaign", createdCampaign.getName());
        assertEquals("Purpose", createdCampaign.getPurpose());
    }


    @Test
    public void testCreateCampaignWithNoPermissions() {
        User user = createUserWithRoleAndPermission1(1L,PermissionEnum.DONATION_APPROVE);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));


        assertThrows(UserPermissionException.class,
                () -> campaignService.createCampaign(1L, "New Campaign", "Purpose"));

    }

    @Test
    public void testCreateCampaignWithNonUniqueName() {
        User user = createUserWithRoleAndPermission1(1L,PermissionEnum.CAMP_MANAGEMENT);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // simulate non-unique name
        Campaign existingCampaign = new Campaign("New Campaign", "Existing Purpose");
        when(campaignRepository.findCampaignByName("New Campaign")).thenReturn(existingCampaign);



        assertThrows(CampaignNameException.class,
                () -> campaignService.createCampaign(1L, "New Campaign", "Purpose"));

    }

    @Test
    public void testCreateCampaignWithUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());


        assertThrows(UserNotFoundException.class,
                () -> campaignService.createCampaign(1L, "New Campaign", "Purpose"));

    }


    @Test
    public void testUpdateCampaignWithValidPermissionsAndUniqueName() throws UserPermissionException, UserNotFoundException, CampaignNameException, CampaignNotFoundException, CampaignRequirementsException {
        User user = createUserWithRoleAndPermission1(1L,PermissionEnum.CAMP_MANAGEMENT);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // Simulating unique name
        when(campaignRepository.findCampaignByName(anyString())).thenReturn(null);
        Campaign existingCampaign = new Campaign("Existing Campaign", "Existing Purpose");
        when(campaignRepository.findById(anyLong())).thenReturn(Optional.of(existingCampaign));

        Campaign updatedCampaign = campaignService.updateCampaign(1L, 1L, "New Campaign", "New Purpose");

        assertNotNull(updatedCampaign);
        assertEquals("New Campaign", updatedCampaign.getName());
        assertEquals("New Purpose", updatedCampaign.getPurpose());
    }

    @Test
    public void testUpdateCampaignWithNoPermissions() {
        User mockUser = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        assertThrows(UserPermissionException.class,
                () -> campaignService.updateCampaign(1L, 1L, "New Campaign", "New Purpose"));
    }

    @Test
    public void testUpdateCampaignWithNonUniqueName() {
        User user = createUserWithRoleAndPermission1(1L, PermissionEnum.CAMP_MANAGEMENT);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        //simulate non-unique name
        Campaign existingCampaign = new Campaign("Existing Campaign", "Existing Purpose");
//        when(campaignRepository.findCampaignByName("New Campaign")).thenReturn(null); // Simulating unique name
        when(campaignRepository.findById(anyLong()))
                .thenAnswer(invocation -> {
                    Long idArg = invocation.getArgument(0);
                    if (idArg.equals(existingCampaign.getId())) {
                        return Optional.of(existingCampaign);
                    }
                    return Optional.empty();
                });

        assertThrows(CampaignNotFoundException.class,
                () -> campaignService.updateCampaign(1L, 1L, "New Campaign", "New Purpose"));

    }

    @Test
    public void testUpdateCampaignWithCampaignNotFound() {
        User user = createUserWithRoleAndPermission1(1L,PermissionEnum.CAMP_MANAGEMENT);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        //  not found scenario
        when(campaignRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CampaignNotFoundException.class,
                () -> campaignService.updateCampaign(1L, 1L, "New Campaign", "New Purpose"));
    }

    @Test
    public void testUpdateCampaignWithUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> campaignService.updateCampaign(1L, 1L, "New Campaign", "New Purpose"));
    }
}
