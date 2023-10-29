package donatorTests;

import demo.msg.javatraining.donationmanager.exceptions.donator.DonatorIdException;
import demo.msg.javatraining.donationmanager.exceptions.donator.DonatorNotFoundException;
import demo.msg.javatraining.donationmanager.exceptions.donator.DonatorRequirementsException;
import demo.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import demo.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import demo.msg.javatraining.donationmanager.persistence.model.ERole;
import demo.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import demo.msg.javatraining.donationmanager.persistence.model.Role;
import demo.msg.javatraining.donationmanager.persistence.model.user.User;
import demo.msg.javatraining.donationmanager.persistence.repository.DonorRepository;
import demo.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import demo.msg.javatraining.donationmanager.service.LogService;
import demo.msg.javatraining.donationmanager.service.donorService.DonorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
//
public class DonorServiceTest {

    @InjectMocks
    private DonorService donorService;

    @Mock
    private LogService logService;

    @Mock
    private DonorRepository donorRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User goodUser(Long userId) {
        PermissionEnum permission = PermissionEnum.BENEF_MANAGEMENT;
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

    private Donor makeDonator(Long donatorId) {
        Donor donor = new Donor("fn1", "ln1", "adn1", "mdn1");
        donor.setId(donatorId);
        return donor;
    }

    private Donor makeBadDonator() {
        return new Donor();
    }

    @Test
    public void testGetAllDonators() {
        List<Donor> donors = new ArrayList<>(
                Arrays.asList(
                        new Donor("fn1", "ln1", "adn1", "mdn1"),
                        new Donor("fn2", "ln2", "adn2", "mdn2")
                )
        );

        when(donorRepository.findAll()).thenReturn(donors);

        List<Donor> donorList = donorRepository.findAll();
        assertNotNull(donorList);
        assertFalse(donorList.isEmpty());
        assertEquals(2, donorList.size());
        assertEquals("fn1", donorList.get(0).getFirstName());
        assertEquals("ln2", donorList.get(1).getLastName());
    }

    @Test
    public void testGetDonatorById() throws DonatorNotFoundException {
        Donor donor = new Donor();
        donor.setId(1L);
        donor.setFirstName("fn1");
        donor.setLastName("ln1");
        donor.setAdditionalName("adn1");
        donor.setMaidenName("mdn1");

        when(donorRepository.findById(1L)).thenReturn(Optional.of(donor));

        Optional<Donor> don = Optional.ofNullable(donorService.getDonatorById(1L));
        assertNotNull(don);
        don.ifPresent(value -> assertEquals("fn1", value.getFirstName()));
        don.ifPresent(value -> assertEquals("ln1", value.getLastName()));
        don.ifPresent(value -> assertEquals("adn1", value.getAdditionalName()));
        don.ifPresent(value -> assertEquals("mdn1", value.getMaidenName()));
    }

    @Test
    public void testCreateDonator() throws
            UserPermissionException,
            DonatorRequirementsException {
        Donor donor = makeDonator(1L);
        User goodUser = goodUser(1L);
        User badUser = badUser(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(goodUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(badUser));
        when(donorRepository.save(donor)).thenReturn(donor);

        Donor createdDonor = donorService.createDonator(goodUser.getId(), donor);

        assertNotNull(createdDonor);
        assertThrows(DonatorRequirementsException.class, () -> {
            donorService.createDonator(goodUser.getId(), new Donor(null, "ln", "s", "d"));
        });
        assertThrows(UserPermissionException.class, () -> {
           donorService.createDonator(badUser.getId(), new Donor("fn1", "ln1", "adn1", "mdn1"));
        });
    }

    @Test
    public void testDeleteDonatorById() throws
            DonatorIdException,
            UserPermissionException,
            DonatorNotFoundException {

        Donor donor = makeDonator(1L);
        Donor don = makeDonator(null);
        User goodUser = goodUser(1L);
        User badUser = badUser(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(goodUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(badUser));
        when(donorRepository.findById(1L)).thenReturn(Optional.of(donor));
        when(donorRepository.deleteDonatorById(1L)).thenReturn(Optional.of(donor));

        assertEquals(donorRepository.deleteDonatorById(1L), Optional.of(donor));

        Donor deletedDonor = donorService.deleteDonatorById(goodUser.getId(), donor.getId());
        verify(donorRepository).deleteById(1L);

        assertThrows(DonatorNotFoundException.class, () -> {
            donorService.deleteDonatorById(goodUser.getId(), 25L);
        });

        assertThrows(DonatorIdException.class, () -> {
            donorService.deleteDonatorById(goodUser.getId(), don.getId());
        });

        assertThrows(UserPermissionException.class, () -> {
            donorService.deleteDonatorById(badUser.getId(), donor.getId());
        });
    }

    @Test
    public void testUpdateDonator() throws
            DonatorIdException,
            UserPermissionException,
            DonatorRequirementsException,
            DonatorNotFoundException {

        Donor donor = makeDonator(1L);
        Donor updon = new Donor("upfn", "upln", "upadnm", "upmdn");
        updon.setId(1L);
        Donor don = makeDonator(null);
        User goodUser = goodUser(1L);
        User badUser = badUser(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(goodUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(badUser));
        when(donorRepository.findById(1L)).thenReturn(Optional.of(donor));
        when(donorRepository.save(donor)).thenReturn(donor);

        // everything works
        assertEquals(donorService.updateDonator(1L, 1L, updon), donor);

        // donor id is null
        assertThrows(DonatorIdException.class, () -> {
            donorService.updateDonator(goodUser.getId(), don.getId(), updon);
        });

        // donor id not found
        assertThrows(DonatorNotFoundException.class, () -> {
            donorService.updateDonator(goodUser.getId(), 25L, updon);
        });

        // user without permission is trying to udpate donor
        assertThrows(UserPermissionException.class, () -> {
            donorService.updateDonator(badUser.getId(), donor.getId(), updon);
        });
    }
}
