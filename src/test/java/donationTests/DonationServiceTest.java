package donationTests;

import demo.msg.javatraining.donationmanager.exceptions.donation.*;
import demo.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import demo.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import demo.msg.javatraining.donationmanager.persistence.donationModel.Donation;
import demo.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import demo.msg.javatraining.donationmanager.persistence.model.ERole;
import demo.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import demo.msg.javatraining.donationmanager.persistence.model.Role;
import demo.msg.javatraining.donationmanager.persistence.model.user.User;
import demo.msg.javatraining.donationmanager.persistence.repository.CampaignRepository;
import demo.msg.javatraining.donationmanager.persistence.repository.DonationRepository;
import demo.msg.javatraining.donationmanager.persistence.repository.DonorRepository;
import demo.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import demo.msg.javatraining.donationmanager.service.LogService;
import demo.msg.javatraining.donationmanager.service.donationService.DonationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DonationServiceTest {
    @InjectMocks
    private DonationService donationService;

    @Mock
    private LogService logService;

    @Mock
    private DonationRepository donationRepository;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private DonorRepository donorRepository;
    @Mock
    private UserRepository userRepository;

    private final PermissionEnum permission = PermissionEnum.DONATION_MANAGEMENT;

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

        User user = new User(userId,"testUser1", "testUser1", "1234567899", "goodUser", "test1@example.com", "psswd1", true, false, 1, roles, new HashSet<>());
        return user;
    }

    private User badUser(Long userId) {
        PermissionEnum permission = PermissionEnum.CAMP_REPORTING;
        Set<PermissionEnum> permissionEnums = new HashSet<>();
        permissionEnums.add(permission);

        Set<Role> roles = new HashSet<>();
        Role role = new Role(1, ERole.ROLE_ADM, permissionEnums);
        roles.add(role);

        User user = new User(userId,"testuser2", "testUser2", "1234567800", "badUser", "test2@example.com", "psswd2", true, false, 1, roles, new HashSet<>());
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

        List<Donation> donations = new ArrayList<>(
                Arrays.asList(
                        new Donation(1L,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null),
                        new Donation(2L,300, "USD", campaign, donor, user, null, "", LocalDate.now(), false, null)
                )
        );

        when(donationRepository.findAll()).thenReturn(donations);

        List<Donation> donationList = donationRepository.findAll();
        assertNotNull(donationList);
        assertFalse(donationList.isEmpty());
        assertEquals(2, donationList.size());
        assertEquals("EUR", donationList.get(0).getCurrency());
        assertEquals(300, donationList.get(1).getAmount());
    }

    @Test
    public void testGetDonationById() throws DonationNotFoundException {
        User user = goodUser(1L);
        Donor donor = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);

        when(donationRepository.findById(eq(user.getId()))).thenReturn(Optional.of(donation));

        Optional<Donation> optDonation = Optional.ofNullable(donationService.getDonationById(1L));
        assertNotNull(optDonation);
        optDonation.ifPresent(value -> assertEquals(1L, value.getId()));
        optDonation.ifPresent(value -> assertEquals(200, value.getAmount()));
        optDonation.ifPresent(value -> assertEquals("EUR", value.getCurrency()));
    }

    @Test
    public void testCreateDonation() throws
            UserPermissionException,
            DonationRequirementsException,
            DonationException {

        User user = goodUser(1L);
        User badUser = badUser(2L);
        Donor donor = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);


        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));
        when(donorRepository.findById(eq(donor.getId()))).thenReturn(Optional.of(donor));
        when(campaignRepository.findById(eq(campaign.getId()))).thenReturn(Optional.of(campaign));
        when(donationRepository.save(donation)).thenReturn(donation);

        Donation createdDonation = donationService.createDonation(user.getId(), donor.getId(), campaign.getId(), donation);

        // works
        assertNotNull(createdDonation);

        // donation requirements not met
        assertThrows(DonationRequirementsException.class, () -> {
            donationService.createDonation(user.getId(), donor.getId(), campaign.getId(), new Donation());
        });

        // user without permission
        assertThrows(UserPermissionException.class, () -> {
            donationService.createDonation(badUser.getId(), donor.getId(), campaign.getId(), donation);
        });

    }

    @Test
    public void testUpdateDonation() throws
            UserPermissionException,
            DonationIdException,
            DonationRequirementsException,
            DonationApprovedException,
            DonationNotFoundException {

        User user = goodUser(1L);
        User badUser = badUser(2L);
        Donor donor = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);
        Donation updatedDonation = new Donation(donation.getId(),550, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);
        Donation updatedDonation2 = new Donation(donation.getId(), 0, null, null, null, null, null, null, null, false, null);
        Donation nullDonation = new Donation(null,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);


        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(badUser));
        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));
        when(donationRepository.save(donation)).thenReturn(donation);
        //when(donationService.checkDonationStatus(1L)).thenReturn(donation.isApproved());

        // everything works
        assertEquals(donationService.updateDonation(1L, donation.getId(), updatedDonation), donation);

        // can't modify an approved donation
        donation.setApproved(true);
        assertThrows(DonationApprovedException.class, () -> {
           donationService.updateDonation(1L, donation.getId(), updatedDonation);
        });

        // updatedDonation requirements not met
        assertThrows(DonationRequirementsException.class, () -> {
            donationService.updateDonation(1L, donation.getId(), updatedDonation2);
        });

        // no permission for user
        assertThrows(UserPermissionException.class, () -> {
            donationService.updateDonation(badUser.getId(), donation.getId(), updatedDonation);
        });

        // donation id null
        assertThrows(DonationIdException.class, () -> {
            donationService.updateDonation(user.getId(), nullDonation.getId(), updatedDonation);
        });

        // donation does not exist
        assertThrows(DonationNotFoundException.class, () -> {
            donationService.updateDonation(user.getId(), 25L, updatedDonation);
        });

    }

    @Test
    public void testDeleteDonationById() throws
            UserPermissionException,
            DonationIdException,
            DonationApprovedException,
            DonationNotFoundException {

        User user = goodUser(1L);
        User badUser = badUser(2L);
        Donor donor = createDonator(1L);
        Campaign campaign = createCampaign(1L);
        Donation donation = new Donation(1L,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);
        Donation nullDonation = new Donation(null,200, "EUR", campaign, donor, user, null, "", LocalDate.now(), false, null);



        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(badUser));
        doNothing().when(donationRepository).deleteById(1L);

        Donation deletedDonation = donationService.deleteDonationById(user.getId(), donation.getId());
        verify(donationRepository).deleteById(1L);

        // user does not have permission
        assertThrows(UserPermissionException.class, () -> {
            donationService.deleteDonationById(badUser.getId(), donation.getId());
        });

        // donation does not exist
        assertThrows(DonationNotFoundException.class, () -> {
            donationService.deleteDonationById(user.getId(), 25L);
        });

        // donation id can't be null
        assertThrows(DonationIdException.class, () -> {
            donationService.deleteDonationById(badUser.getId(), nullDonation.getId());
        });

        // can't delete an approved donation
        donation.setApproved(true);
        assertThrows(DonationApprovedException.class, () -> {
            donationService.deleteDonationById(user.getId(), donation.getId());
        });
    }
}
