package campaignTests;

import de.msg.javatraining.donationmanager.controller.campaign.CampaignController;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignIdException;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignNameException;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.campaign.CampaignRequirementsException;
import de.msg.javatraining.donationmanager.exceptions.user.UserIdException;
import de.msg.javatraining.donationmanager.exceptions.user.UserNotFoundException;
import de.msg.javatraining.donationmanager.exceptions.user.UserPermissionException;
import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.service.campaignService.CampaignService;
import de.msg.javatraining.donationmanager.service.donationService.DonationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class CampaignControllerTest {

    @InjectMocks
    private CampaignController campaignController;

    @Mock
    private CampaignService campaignService;

    @Mock
    private DonationService donationService;

    @Test
    public void testGetAllCampaigns() {
        List<Campaign> mockCampaigns = Arrays.asList(
                new Campaign("Campaign 1", "Purpose 1"),
                new Campaign("Campaign 2", "Purpose 2")
        );
        when(campaignService.getAllCampaigns()).thenReturn(mockCampaigns);

        List<Campaign> result = campaignController.getALlCampaigns();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Campaign 1", result.get(0).getName());
        assertEquals("Purpose 2", result.get(1).getPurpose());
    }

    @Test
    public void testCreateCampaign() throws UserPermissionException, UserNotFoundException, CampaignNameException, CampaignRequirementsException {
        Campaign mockCampaign = new Campaign("New Campaign", "New Purpose");
        Long userId = 1L;

        when(campaignService.createCampaign(eq(userId), anyString(), anyString())).thenReturn(mockCampaign);

        ResponseEntity<?> response = campaignController.createCapmaign(userId, mockCampaign);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateCampaign() throws UserPermissionException, UserNotFoundException, CampaignNameException, CampaignNotFoundException, CampaignRequirementsException {
        // Prepare mock campaign and user IDs
        Campaign mockCampaign = new Campaign("Updated Campaign", "Updated Purpose");
        Long userId = 1L;
        Long campId = 2L;

        when(campaignService.updateCampaign(eq(userId), eq(campId), anyString(), anyString())).thenReturn(mockCampaign);

        ResponseEntity<?> response = campaignController.updateCampaign(campId, userId, mockCampaign);


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteCampaignById() throws UserPermissionException, CampaignIdException, CampaignNotFoundException, UserIdException {
        Long userId = 1L;
        Long campId = 2L;

        when(donationService.findDonationsByCampaignId(eq(campId))).thenReturn(false);
        when(campaignService.deleteCampaignById(eq(userId), eq(campId))).thenReturn(new Campaign());

        ResponseEntity<?> response = campaignController.deleteCampaignById(campId, userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Campaign deleted successfully", response.getBody());
    }

    @Test
    public void testDeleteCampaignWithPaidDonations() {
        Long userId = 1L;
        Long campId = 2L;

        when(donationService.findDonationsByCampaignId(eq(campId))).thenReturn(true);

        ResponseEntity<?> response = campaignController.deleteCampaignById(campId, userId);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Deletion failed: Campaign has paid Donations", response.getBody());
    }
}
