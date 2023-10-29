package tests;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import demo.msg.javatraining.donationmanager.exceptions.permission.PermissionException;
import demo.msg.javatraining.donationmanager.persistence.model.ERole;
import demo.msg.javatraining.donationmanager.service.LogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import demo.msg.javatraining.donationmanager.controller.permission.PermissionController;
import demo.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import demo.msg.javatraining.donationmanager.persistence.model.Role;
import demo.msg.javatraining.donationmanager.service.permissionService.PermissionService;

import java.util.HashSet;

@ExtendWith(MockitoExtension.class)
class PermissionControllerTest {

    @InjectMocks
    private PermissionController permissionController;

    @Mock
    private PermissionService permissionService;

    @Mock
    private LogService logService;

    @Test
    void testAddPermissionToRole_Success() throws PermissionException {
        // Mocking
        Role mockRole = new Role(1, ERole.ROLE_ADM, new HashSet<>());
        when(permissionService.addPermissionToRole(anyLong(), anyInt(), any(PermissionEnum.class))).thenReturn(mockRole);

        // Test
        ResponseEntity<?> response = permissionController.addPermissionToRole(1L, 1, PermissionEnum.CAMP_MANAGEMENT);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Role);
        Role resultRole = (Role) response.getBody();
        assertEquals(mockRole, resultRole);
    }

    @Test
    void testDeletePermissionFromRole_Success() throws PermissionException {
        // Mocking
        Role mockRole = new Role(1, ERole.ROLE_ADM, new HashSet<>());
        when(permissionService.deletePermissionFromRole(anyLong(), anyInt(), any(PermissionEnum.class))).thenReturn(mockRole);

        // Test
        ResponseEntity<?> response = permissionController.deletePermissionFromRole(1L, 1, PermissionEnum.CAMP_MANAGEMENT);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Role);
        Role resultRole = (Role) response.getBody();
        assertEquals(mockRole, resultRole);
    }
}
