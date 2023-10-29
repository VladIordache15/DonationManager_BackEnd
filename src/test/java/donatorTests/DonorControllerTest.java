package donatorTests;

import demo.msg.javatraining.donationmanager.controller.donor.DonorController;
import demo.msg.javatraining.donationmanager.exceptions.donator.DonatorIdException;
import demo.msg.javatraining.donationmanager.exceptions.donator.DonatorNotFoundException;
import demo.msg.javatraining.donationmanager.exceptions.donator.DonatorRequirementsException;
import demo.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import demo.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import demo.msg.javatraining.donationmanager.persistence.model.ERole;
import demo.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import demo.msg.javatraining.donationmanager.persistence.model.Role;
import demo.msg.javatraining.donationmanager.persistence.model.user.User;
import demo.msg.javatraining.donationmanager.service.donationService.DonationService;
import demo.msg.javatraining.donationmanager.service.donorService.DonorService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DonorControllerTest {
    @InjectMocks
    private DonorController donorController;

    @Mock
    private DonorService donorService;

    @Mock
    private DonationService donationService;

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

    private Donor createGoodDonator(Long donatorId) {
        Donor donor = new Donor("gfn1", "gln1", "gadn1", "gmdn1");
        donor.setId(donatorId);
        return donor;
    }

    private Donor createBadDonator() {
        Donor donor = new Donor("bfn1", "bln1", "badn1", "bmdn1");
        donor.setId(null);
        return donor;
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllDonators() {
        List<Donor> mockDonors = Arrays.asList(
                new Donor("fn1", "ln1", "adn1", "mdn1"),
                new Donor("fn2", "ln2", "adn2", "mdn2"),
                new Donor("fn3", "ln3", "adn3", "mdn3"),
                new Donor("fn4", "ln4", "adn4", "mdn4")
        );
        when(donorService.getAllDonators()).thenReturn(mockDonors);

        List<Donor> result = donorController.getAllDonators();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("fn1", result.get(0).getFirstName());
        assertEquals("ln2", result.get(1).getLastName());
        assertEquals("adn3", result.get(2).getAdditionalName());
        assertEquals("mdn4", result.get(3).getMaidenName());
    }

    @Test
    public void testGetDonator() throws DonatorNotFoundException {
        Donor donor = createGoodDonator(1L);

        when(donorService.getDonatorById(donor.getId())).thenReturn(donor);

        ResponseEntity<?> resultDonator = donorController.getDonator(donor.getId());
        Donor result = (Donor) resultDonator.getBody();

        assertNotNull(resultDonator);
        assertEquals(result.getId(), donor.getId());
        assertEquals(result.getFirstName(), donor.getFirstName());
        assertEquals(result.getLastName(), donor.getLastName());
        assertEquals(result.getAdditionalName(), donor.getAdditionalName());
        assertEquals(result.getMaidenName(), donor.getMaidenName());

    }


    @Test
    public void testCreateDonator() throws UserPermissionException, DonatorRequirementsException {
        Donor donator = createGoodDonator(1L);
        Donor badDonator = new Donor();
        User goodUser = goodUser(1L);
        User badUser = badUser(2L);

        // success
        when(donorService.createDonator(goodUser.getId(), donator)).thenReturn(donator);

        // no permission
        when(donorService.createDonator(badUser.getId(), donator)).thenThrow(new UserPermissionException());

        // donator req
        when(donorService.createDonator(goodUser.getId(), badDonator)).thenThrow(new DonatorRequirementsException());

        ResponseEntity<?> goodResponse = donorController.createDonator(goodUser.getId(), donator);
        assertNotNull(goodResponse);
        assertEquals(HttpStatus.OK, goodResponse.getStatusCode());
        assertEquals(donator, goodResponse.getBody());

        // user with no permission
        ResponseEntity<?> permissionResponse = donorController.createDonator(badUser.getId(), donator);
        assertNotNull(permissionResponse);
        assertEquals(HttpStatus.OK, permissionResponse.getStatusCode());
        assertTrue(permissionResponse.getBody() instanceof UserPermissionException);

        // donator requirements not met
        ResponseEntity<?> requirementsResponse = donorController.createDonator(goodUser.getId(), badDonator);
        assertNotNull(requirementsResponse);
        assertEquals(HttpStatus.OK, requirementsResponse.getStatusCode());
        assertTrue(requirementsResponse.getBody() instanceof DonatorRequirementsException);

    }

    @Test
    public void testUpdateDonator() throws
            DonatorIdException,
            UserPermissionException,
            DonatorRequirementsException,
            DonatorNotFoundException {

        Long invalidId = 25L;
        Donor donor = createGoodDonator(1L);
        Donor badDonor = createBadDonator();
        Donor updatedDonor = new Donor("ugfn1", "ugln1", "ugadn1", "ugmdn1");
        Donor badUpdatedDonor = new Donor(null, null, null, null);
        User goodUser = goodUser(1L);
        User badUser = badUser(2L);


        // Mock services

        when(donorService.updateDonator(goodUser.getId(), donor.getId(), updatedDonor)).
                thenReturn(donor);

        when(donorService.updateDonator(badUser.getId(), donor.getId(), updatedDonor)).
                thenThrow(new UserPermissionException());

        when (donorService.updateDonator(goodUser.getId(), donor.getId(), badUpdatedDonor)).
                thenThrow(new DonatorRequirementsException());

        when(donorService.updateDonator(goodUser.getId(), invalidId, updatedDonor)).
                thenThrow(new DonatorNotFoundException());

        when(donorService.updateDonator(goodUser.getId(), badDonor.getId(), updatedDonor)).
                thenThrow(new DonatorIdException());

        // Success
        // Call the controller method & verify the response
        ResponseEntity<?> responseEntity = donorController.updateDonator(goodUser.getId(), donor.getId(), updatedDonor);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(donor, responseEntity.getBody());

        // User exception
        // Call the controller method & verify the response
        ResponseEntity<?> userEntity = donorController.updateDonator(badUser.getId(), donor.getId(), updatedDonor);
        assertNotNull(userEntity);
        assertEquals(HttpStatus.OK, userEntity.getStatusCode());
        assertTrue(userEntity.getBody() instanceof UserPermissionException);

        // DonatorId exception
        // Call the controller method & verify the response
        ResponseEntity<?> didEntity = donorController.updateDonator(goodUser.getId(), badDonor.getId(), updatedDonor);
        assertNotNull(didEntity);
        assertEquals(HttpStatus.OK, didEntity.getStatusCode());
        assertTrue(didEntity.getBody() instanceof DonatorIdException);

        // DonatorReq exception
        // Call the controller method & verify the response
        ResponseEntity<?> reqEntity = donorController.updateDonator(goodUser.getId(), donor.getId(), badUpdatedDonor);
        assertNotNull(reqEntity);
        assertEquals(HttpStatus.OK, reqEntity.getStatusCode());
        assertTrue(reqEntity.getBody() instanceof DonatorRequirementsException);

        // Donor not found
        // Call the controller method & verify the response
        ResponseEntity<?> notFoundEntity = donorController.updateDonator(goodUser.getId(), invalidId, updatedDonor);
        assertNotNull(notFoundEntity);
        assertEquals(HttpStatus.OK, notFoundEntity.getStatusCode());
        assertTrue(notFoundEntity.getBody() instanceof DonatorNotFoundException);
    }

    @Test
    public void testDeleteDonatorById() throws
            DonatorIdException,
            UserPermissionException,
            DonatorNotFoundException {

        Long invalidId = 25L;
        User goodUser = goodUser(1L);
        User badUser = badUser(2L);
        Donor testDonor = createGoodDonator(1L);

        Donor noDonationsDonor = createGoodDonator(2L);
        Donor unknowDonor = new Donor("UNKNOWN", "UNKNOWN", "UNKNOWN", "UNKNOWN");
        unknowDonor.setId(3L);

        // Mock the behavior of the service methods
        when(donorService.deleteDonatorById(goodUser.getId(), testDonor.getId())).thenReturn(testDonor);
        when(donorService.deleteDonatorById(goodUser.getId(), noDonationsDonor.getId())).thenReturn(noDonationsDonor);
        when(donorService.deleteDonatorById(goodUser.getId(), null)).thenThrow(new DonatorIdException());
        when(donorService.deleteDonatorById(badUser.getId(), testDonor.getId())).thenThrow(new UserPermissionException());
        when(donorService.deleteDonatorById(goodUser.getId(), invalidId)).thenThrow(new DonatorNotFoundException());

        ResponseEntity<?> goodResponse = donorController.deleteDonatorById(goodUser.getId(), testDonor.getId());
        assertEquals(goodResponse.getBody(), testDonor);

        ResponseEntity<?> goodResponse2 = donorController.deleteDonatorById(goodUser.getId(), noDonationsDonor.getId());
        assertEquals(goodResponse2.getBody(), noDonationsDonor);

        // User exception
        // Call the controller method & verify the response
        ResponseEntity<?> userEntity = donorController.deleteDonatorById(badUser.getId(), testDonor.getId());
        assertNotNull(userEntity);
        assertEquals(HttpStatus.OK, userEntity.getStatusCode());
        assertTrue(userEntity.getBody() instanceof UserPermissionException);

        // DonorId exception
        // Call the controller method & verify the response
        ResponseEntity<?> didEntity = donorController.deleteDonatorById(goodUser.getId(), null);
        assertNotNull(didEntity);
        assertEquals(HttpStatus.OK, didEntity.getStatusCode());
        assertTrue(didEntity.getBody() instanceof DonatorIdException);

        // Donor not found
        // Call the controller method & verify the response
        ResponseEntity<?> notFoundEntity = donorController.deleteDonatorById(goodUser.getId(), invalidId);
        assertNotNull(notFoundEntity);
        assertEquals(HttpStatus.OK, notFoundEntity.getStatusCode());
        assertTrue(notFoundEntity.getBody() instanceof DonatorNotFoundException);

    }

}
