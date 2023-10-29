package tests;

import demo.msg.javatraining.donationmanager.exceptions.permission.PermissionException;
import demo.msg.javatraining.donationmanager.persistence.model.ERole;
import demo.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import demo.msg.javatraining.donationmanager.persistence.model.Role;
import demo.msg.javatraining.donationmanager.persistence.model.user.User;
import demo.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import demo.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import demo.msg.javatraining.donationmanager.service.LogService;
import demo.msg.javatraining.donationmanager.service.permissionService.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class) // This annotation initializes the Mockito extensions for JUnit 5
class PermissionServiceTest {
    @InjectMocks
    private PermissionService permissionService;
    @Mock
    private  RoleRepository roleRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private LogService logService;

    //@Mock
    //private PermissionRepository permissionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User createUserWithRoleAndPermission1(Long userId) {
        Set<PermissionEnum> permissionEnums = new HashSet<>();
        permissionEnums.add(PermissionEnum.PERMISSION_MANAGEMENT);
        permissionEnums.add(PermissionEnum.USER_MANAGEMENT);
        Set<Role> roles = new HashSet<>();
        Role role = new Role(1, ERole.ROLE_ADM, permissionEnums);
        roles.add(role);

        User user = new User(userId,"testuser", "test", "1234567890", "something", "test@example.com", "psswd", true, false, 1, roles, new HashSet<>());
        return user;
    }

    private User createUserWithRoleAndPermission2(Long userId) {
        Set<PermissionEnum> permissionEnums = new HashSet<>();
        permissionEnums.add(PermissionEnum.CAMP_MANAGEMENT);
        permissionEnums.add(PermissionEnum.USER_MANAGEMENT);
        Set<Role> roles = new HashSet<>();
        Role role = new Role(1, ERole.ROLE_ADM, permissionEnums);
        roles.add(role);

        when(roleRepository.save(role)).thenReturn(role);
        User user = new User(userId,"testuser2", "test2", "7234567890", "something2", "test2@example.com", "psswd2", true, false, 1, roles, new HashSet<>());
        return user;
    }

    @Test
    public void testAddPermissionToRole() throws PermissionException {
        User mockUser = createUserWithRoleAndPermission1(1L);
        Role mockRole = mockUser.getRoles().iterator().next(); // Get the mock role from the user

        PermissionEnum permissionToAdd = PermissionEnum.CAMP_MANAGEMENT; // Choose a permission to add

        // Set up mock behavior
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(roleRepository.findById(anyInt())).thenReturn(Optional.of(mockRole));

        permissionService.addPermissionToRole(1L, 1, permissionToAdd);

        assertTrue(mockRole.getPermissions().contains(permissionToAdd));
    }

    @Test
    public void testDeletePermissionFromRole() throws PermissionException {
        User mockUser = createUserWithRoleAndPermission1(1L);
        Set<PermissionEnum> permissionEnumSet = new HashSet<>();
        permissionEnumSet.add(PermissionEnum.CAMP_MANAGEMENT);
        permissionEnumSet.add(PermissionEnum.BENEF_MANAGEMENT);
        Role mockRole = new Role(3, ERole.ROLE_REP,permissionEnumSet);
        roleRepository.save(mockRole);

        PermissionEnum permissionToDelete = PermissionEnum.CAMP_MANAGEMENT; // Choose a permission to delete

        // Set up mock behavior
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(roleRepository.findById(anyInt())).thenReturn(Optional.of(mockRole));

        permissionService.deletePermissionFromRole(1L, 3, permissionToDelete);

        assertFalse(mockRole.getPermissions().contains(permissionToDelete));
    }



    @Test
    void testHasPermission() {
        // Create a user with a role containing the permission
        User user = createUserWithRoleAndPermission1(1L);

        boolean hasPermission = permissionService.hasPermission(user, PermissionEnum.PERMISSION_MANAGEMENT);
        assertTrue(hasPermission);

        boolean hasNoPermission = permissionService.hasPermission(user, PermissionEnum.CAMP_REPORT_RESTRICTED);
        assertFalse(hasNoPermission);
    }

    @Test
    void testGetRoles() {
        // Create roles with the necessary permissions
        Set<PermissionEnum> permissionEnums = new HashSet<>();
        permissionEnums.add(PermissionEnum.PERMISSION_MANAGEMENT);
        permissionEnums.add(PermissionEnum.USER_MANAGEMENT);

        Role role = new Role(1, ERole.ROLE_ADM, permissionEnums);

        // Create a user and add the role
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setMobileNumber("1234567890");
        user.setPassword("testpassword");
        user.setRoles(new HashSet<>(Collections.singletonList(role))); // Add the role

        // Mock the behavior of userRepository.findById
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Set<Role> roles = permissionService.getRoles(1L);
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
        assertEquals(user.getRoles(), roles);
    }
}
