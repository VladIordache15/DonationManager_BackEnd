package donationTests;

import demo.msg.javatraining.donationmanager.controller.donation.DonationController;
import demo.msg.javatraining.donationmanager.exceptions.donation.*;
import demo.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import demo.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import demo.msg.javatraining.donationmanager.persistence.donationModel.Donation;
import demo.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import demo.msg.javatraining.donationmanager.persistence.model.ERole;
import demo.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import demo.msg.javatraining.donationmanager.persistence.model.Role;
import demo.msg.javatraining.donationmanager.persistence.model.user.User;
import demo.msg.javatraining.donationmanager.service.campaignService.CampaignService;
import demo.msg.javatraining.donationmanager.service.donationService.DonationService;
import demo.msg.javatraining.donationmanager.service.donorService.DonorService;
import demo.msg.javatraining.donationmanager.service.userService.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class DonationControllerTest {
    @InjectMocks
    private DonationController donationController;

    @Mock
    private DonorService donorService;
    @Mock
    private UserService userService;
    @Mock
    private CampaignService campaignService;
    @Mock
    private DonationService donationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User goodUser(Long userId) {
        PermissionEnum permission = PermissionEnum.DONATION_MANAGEMENT;
        Set<PermissionEnum> permissionEnums = new HashSet<>();
        permissionEnums.add(permission);

        Set<Role> roles = new HashSet<>();
        Role role = new Role(1, ERole.ROLE_ADM, permissionEnums);
        roles.add(role);

        User user = new User(userId, "testUser1", "testUser1", "1234567899", "goodUser", "test1@example.com", "psswd1", true, false, 1, roles, new HashSet<>());
        return user;
    }

    private User badUser(Long userId) {
        PermissionEnum permission = PermissionEnum.CAMP_REPORTING;
        Set<PermissionEnum> permissionEnums = new HashSet<>();
        permissionEnums.add(permission);

        Set<Role> roles = new HashSet<>();
        Role role = new Role(1, ERole.ROLE_ADM, permissionEnums);
        roles.add(role);

        User user = new User(userId, "testuser2", "testUser2", "1234567800", "badUser", "test2@example.com", "psswd2", true, false, 1, roles, new HashSet<>());
        return user;
    }

    private Donor createDonator(Long donatorId) {
        Donor donor = new Donor("fn1", "ln1", "adn1", "mdn1");
        donor.setId(donatorId);
        return donor;
    }

    private Campaign createCampaign(Long campaignId) {
        Campaign campaign = new Campaign("c1", "p1");
        campaign.setId(campaignId);
        return campaign;
    }

    @Test
    public void testGetAllDonations() {
        User user = goodUser(1L);
        Donor donor = createDonator(1L);
        Campaign campaign = createCampaign(1L);

        List<Donation> mockDonations = Arrays.asList(
                new Donation(1L, 200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null),
                new Donation(2L, 250, "USD", campaign, donor, user, null, "", LocalDate.now(), false, null),
                new Donation(3L, 2000, "YEN", campaign, donor, user, null, "", LocalDate.now(), false, null)
        );
        when(donationService.getAllDonations()).thenReturn(mockDonations);

        List<Donation> result = donationController.getAllDonations();
        assertEquals(3, result.size());
        assertEquals("EUR", result.get(0).getCurrency());
        assertEquals(250, result.get(1).getAmount());
        assertEquals(3L, result.get(2).getId());
    }

    @Test
    public void testGetDonation() throws DonationNotFoundException {
        User user = goodUser(1L);
        Donor donor = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L, 200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);

        when(donationService.getDonationById(donation.getId())).thenReturn(donation);

        ResponseEntity<?> responseEntity = donationController.getDonation(donation.getId());
        Donation result = (Donation) responseEntity.getBody();

        assertNotNull(result);
        assertEquals(result.getId(), donation.getId());
        assertEquals(result.getAmount(), donation.getAmount());
        assertEquals(result.getCurrency(), donation.getCurrency());
    }

    @Test
    public void testCreateDonation() throws
            UserPermissionException,
            DonationRequirementsException,
            DonationException {

        User user = goodUser(1L);
        User badUser = badUser(2L);
        Donor donator = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L, 200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);
        Donation noRequirementsDonation = new Donation();
        Donation donationException = new Donation(3L, 200, "EUR", null, null, null, null, "", LocalDate.now(), false, null);

        // Mock service response
        when(donationService.createDonation(user.getId(), donator.getId(), campaign.getId(), donation)).thenReturn(donation);

        // Mock service response to throw UserPermissionException
        when(donationService.createDonation(badUser.getId(), donator.getId(), campaign.getId(), donation))
                .thenThrow(new UserPermissionException("User does not have permission!"));

        // Mock service response to throw DonationRequirementsException
        when(donationService.createDonation(user.getId(), donator.getId(), campaign.getId(), noRequirementsDonation))
                .thenThrow(new DonationRequirementsException("Requirements not met!"));

        when(donationService.createDonation(user.getId(), donator.getId(), campaign.getId(), donationException))
                .thenThrow(new DonationException());

        // Success
        // Call the controller method & verify the response
        ResponseEntity<?> responseEntity = donationController.createDonation(user.getId(), donator.getId(), campaign.getId(), donation);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(donation, responseEntity.getBody());

        // User does not have permission
        // Call the controller method & verify the response
        ResponseEntity<?> permissionResponse = donationController.createDonation(badUser.getId(), donator.getId(), campaign.getId(), donation);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, permissionResponse.getStatusCode());
        assertTrue(permissionResponse.getBody() instanceof UserPermissionException);

        // Requirements of donation haven't been met
        // Call the controller method & verify the response
        ResponseEntity<?> requirementsResponse = donationController.createDonation(user.getId(), donator.getId(), campaign.getId(), noRequirementsDonation);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, requirementsResponse.getStatusCode());
        assertTrue(requirementsResponse.getBody() instanceof DonationRequirementsException);

        // Donation exception
        // Call the controller method & verify the response
        ResponseEntity<?> donationResponse = donationController.createDonation(user.getId(), donator.getId(), campaign.getId(), donationException);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, donationResponse.getStatusCode());
        assertTrue(donationResponse.getBody() instanceof DonationException);

    }

    @Test
    public void testUpdateDonation() throws
            UserPermissionException,
            DonationIdException,
            DonationRequirementsException,
            DonationApprovedException,
            DonationNotFoundException {

        Long invalidDonatonId = 300L;
        User user = goodUser(1L);
        User badUser = badUser(2L);
        Donor donator = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L, 200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);
        Donation badDonation = new Donation(null, 200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);
        Donation apprvedDonation = new Donation(2L, 200, "EUR", campaign, donator, user, null, "", LocalDate.now(), true, null);
        Donation update = new Donation(1L, 300, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);
        Donation badUpdate = new Donation(3L, 0, null, null, null, null, null, null, null, false, null);

        // success
        when(donationService.updateDonation(user.getId(), donation.getId(), update)).thenReturn(donation);

        // Mock service response to throw UserPermissionException
        when(donationService.updateDonation(badUser.getId(), donation.getId(), update))
                .thenThrow(new UserPermissionException());

        // Mock service response to throw DonationIdException
        when(donationService.updateDonation(user.getId(), badDonation.getId(), update))
                .thenThrow(new DonationIdException());

        // Mock service response to throw DonationRequirementsException
        when(donationService.updateDonation(user.getId(), donation.getId(), badUpdate))
                .thenThrow(new DonationRequirementsException());

        // Mock service response to throw DonationApprovedException
        when(donationService.updateDonation(user.getId(), apprvedDonation.getId(), update))
                .thenThrow(new DonationApprovedException());

        // Mock service response to throw DonationNotFoundException
        when(donationService.updateDonation(user.getId(), invalidDonatonId, update))
                .thenThrow(new DonationNotFoundException());

        // Success
        // Call the controller method & verify the response
        ResponseEntity<?> responseEntity = donationController.updateDonation(user.getId(), donation.getId(), update);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(donation, responseEntity.getBody());

        // User exception
        // Call the controller method & verify the response
        ResponseEntity<?> userEntity = donationController.updateDonation(badUser.getId(), donation.getId(), update);
        assertNotNull(userEntity);
        assertEquals(HttpStatus.OK, userEntity.getStatusCode());
        assertTrue(userEntity.getBody() instanceof UserPermissionException);

        // DonationId exception
        // Call the controller method & verify the response
        ResponseEntity<?> didEntity = donationController.updateDonation(user.getId(), badDonation.getId(), update);
        assertNotNull(didEntity);
        assertEquals(HttpStatus.OK, didEntity.getStatusCode());
        assertTrue(didEntity.getBody() instanceof DonationIdException);

        // DonationReqEx exception
        // Call the controller method & verify the response
        ResponseEntity<?> reqEntity = donationController.updateDonation(user.getId(), donation.getId(), badUpdate);
        assertNotNull(reqEntity);
        assertEquals(HttpStatus.OK, reqEntity.getStatusCode());
        assertTrue(reqEntity.getBody() instanceof DonationRequirementsException);

        // Donation already approved
        // Call the controller method & verify the response
        ResponseEntity<?> appEntity = donationController.updateDonation(user.getId(), apprvedDonation.getId(), update);
        assertNotNull(appEntity);
        assertEquals(HttpStatus.OK, appEntity.getStatusCode());
        assertTrue(appEntity.getBody() instanceof DonationApprovedException);

        // Donation not found
        // Call the controller method & verify the response
        ResponseEntity<?> notFoundEntity = donationController.updateDonation(user.getId(), invalidDonatonId, update);
        assertNotNull(notFoundEntity);
        assertEquals(HttpStatus.OK, notFoundEntity.getStatusCode());
        assertTrue(notFoundEntity.getBody() instanceof DonationNotFoundException);
    }


    @Test
    public void testDeleteDonationById() throws
            UserPermissionException,
            DonationIdException,
            DonationApprovedException,
            DonationNotFoundException {

        Long invalidDonatonId = 300L;
        User user = goodUser(1L);
        User badUser = badUser(2L);
        Donor donator = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L, 200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);
        Donation badDonation = new Donation(null, 200, "EUR", campaign, donator, user, null, "", LocalDate.now(), false, null);
        Donation apprvedDonation = new Donation(2L, 200, "EUR", campaign, donator, user, null, "", LocalDate.now(), true, null);

        // success
        when(donationService.deleteDonationById(user.getId(), donation.getId())).thenReturn(donation);

        // Mock service response to throw UserPermissionException
        when(donationService.deleteDonationById(badUser.getId(), donation.getId()))
                .thenThrow(new UserPermissionException());

        // Mock service response to throw DonationIdException
        when(donationService.deleteDonationById(user.getId(), badDonation.getId()))
                .thenThrow(new DonationIdException());

        // Mock service response to throw DonationApprovedException
        when(donationService.deleteDonationById(user.getId(), apprvedDonation.getId()))
                .thenThrow(new DonationApprovedException());

        // Mock service response to throw DonationApprovedException
        when(donationService.deleteDonationById(user.getId(), invalidDonatonId))
                .thenThrow(new DonationNotFoundException());

        // User exception
        // Call the controller method & verify the response
        ResponseEntity<?> userEntity = donationController.deleteDonationById(badUser.getId(), donation.getId());
        assertNotNull(userEntity);
        assertEquals(HttpStatus.OK, userEntity.getStatusCode());
        assertTrue(userEntity.getBody() instanceof UserPermissionException);

        // DonationId exception
        // Call the controller method & verify the response
        ResponseEntity<?> didEntity = donationController.deleteDonationById(user.getId(), badDonation.getId());
        assertNotNull(didEntity);
        assertEquals(HttpStatus.OK, didEntity.getStatusCode());
        assertTrue(didEntity.getBody() instanceof DonationIdException);

        // Donation not found
        // Call the controller method & verify the response
        ResponseEntity<?> notFoundEntity = donationController.deleteDonationById(user.getId(), invalidDonatonId);
        assertNotNull(notFoundEntity);
        assertEquals(HttpStatus.OK, notFoundEntity.getStatusCode());
        assertTrue(notFoundEntity.getBody() instanceof DonationNotFoundException);

        // Donation already approved
        // Call the controller method & verify the response
        ResponseEntity<?> appEntity = donationController.deleteDonationById(user.getId(), apprvedDonation.getId());
        assertNotNull(appEntity);
        assertEquals(HttpStatus.OK, appEntity.getStatusCode());
        assertTrue(appEntity.getBody() instanceof DonationApprovedException);
    }

}
