package de.msg.javatraining.donationmanager.service.permissionService;

import de.msg.javatraining.donationmanager.exceptions.permission.PermissionException;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import de.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import de.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import de.msg.javatraining.donationmanager.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LogService logService;

    public List<PermissionEnum> getAllPermissions(){
        return Arrays.stream(PermissionEnum.values()).toList();
    }

    public List<PermissionEnum> getAllPermissions(Long roleId){
        Optional<Role> role = roleRepository.findById(roleId);
        return role.get().getPermissions().stream().toList();
    }


    /**
     * Returns the modified new Role from the database, which now has also the new permission in the permission list
     * of the role
     * @author Gal Timea
     * @param userId id of the user who is logged in
     * @param roleId id of the role
     * @param permissionToAdd new permission to add that role, specified with the roleId
     * @return new modified Role which now has the new permission
     * @throws PermissionException depending on the case: the new permission is null,
     * the user does not have the specific permission to edit roles or the permission already exists in the permission
     * list of that role, specified with roleId
     */
    public Role addPermissionToRole(Long userId, Integer roleId, PermissionEnum permissionToAdd) throws PermissionException{

        Optional<User> userADMIN = userRepository.findById(userId);
        if (permissionToAdd == null) {
            logService.logOperation("ERROR",  "Permission to add cannot be null.", userADMIN.get().getUsername());
            throw new PermissionException("Permission to add cannot be null.","Permission_to_add_cannot_be_null.");
        }

        Set<PermissionEnum> s = new HashSet<>(); s.add(permissionToAdd);
        // Check if the permission exists in the PermissionRepository
        //if (!permissionRepository.exists(new PermissionEnumWrapper(s))) {
        //    throw new IllegalArgumentException("Permission does not exist.");
        //}
        Optional<Role> role = roleRepository.findById(roleId);

        if (userADMIN.isPresent()) {
            PermissionEnum adminPermissionToCheck = PermissionEnum.PERMISSION_MANAGEMENT;

            for (Role adminRole : userADMIN.get().getRoles()) {
                if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
                    if (role.get().getPermissions().contains(permissionToAdd)) {
                        logService.logOperation("ERROR", "Permission already exists.", userADMIN.get().getUsername());
                        throw new PermissionException("Permission already exists.","Permission_already_exists.");}
                    else {
                        Set<PermissionEnum> permissions = role.get().getPermissions();
                        permissions.add(permissionToAdd);
                        role.get().setPermissions(permissions);
                        logService.logOperation("INSERT", "Added permission", userADMIN.get().getUsername());
                        return roleRepository.save(role.get());
                        //return permissionToAdd;
                    }

                }
            }
        }
        logService.logOperation("ERROR", "User not found or permission not available to edit roles.", userADMIN.get().getUsername());
        throw new PermissionException("User not found or permission not available to edit roles.", "User_not_found_or_permission_not_available_to_edit_roles.");
    }


    /**
     * Returns the modified new Role from the database, which now does not have the deleted permission in the permission list
     * of the role
     * @author Gal Timea
     * @param userId id of the user who is logged in
     * @param roleId id of the role
     * @param permissionToDelete new permission to delete from that role, specified with the roleId
     * @return new modified Role which now does not have the permission
     * @throws PermissionException depending on the case: the new permission is null,
     * the user does not have the specific permission to edit roles or the permission is not available in the permission
     * list of that role, specified with roleId (not available - maybe already deleted)
     */
    public Role deletePermissionFromRole(Long userId, Integer roleId, PermissionEnum permissionToDelete) throws PermissionException {
        Optional<User> userADMIN = userRepository.findById(userId);

        if (permissionToDelete == null) {
            logService.logOperation("ERROR", "Permission to delete cannot be null.", userADMIN.get().getUsername());
            throw new PermissionException("Permission to delete cannot be null.", "Permission_to_delete_cannot_be_null.");
        }

        Set<PermissionEnum> pp = new HashSet<>(); pp.add(permissionToDelete);
        // Check if the permission exists in the PermissionRepository
        //if (!permissionRepository.exists(new PermissionEnumWrapper(pp))) {
        //    throw new IllegalArgumentException("Permission does not exist.");
        //}

        Optional<Role> role = roleRepository.findById(roleId);

        if (userADMIN.isPresent()) {
            PermissionEnum adminPermissionToCheck = PermissionEnum.PERMISSION_MANAGEMENT;

            for (Role adminRole : userADMIN.get().getRoles()) {
                if (adminRole.getPermissions().contains(adminPermissionToCheck)) {
                    if (role.get().getPermissions().contains(permissionToDelete)) {
                        Set<PermissionEnum> permissions = role.get().getPermissions();
                        permissions.remove(permissionToDelete);
                        role.get().setPermissions(permissions);
                        logService.logOperation("DELETE", "Deleted permission", userADMIN.get().getUsername());
                        return roleRepository.save(role.get());
                        //return permissionToDelete;
                    } else {
                        logService.logOperation("ERROR", "Permission to delete does not exist.", userADMIN.get().getUsername());
                        throw new PermissionException("Permission to delete does not exist.", "Permission_to_delete_does_not_exist.");
                    }
                }
            }
        }
        logService.logOperation("ERROR", "User not found or permission not available to edit roles.", userADMIN.get().getUsername());
        throw new PermissionException("User not found or permission not available to edit roles.", "User_not_found_or_permission_not_available_to_edit_roles.");
    }


    /**
     * If the user has the permission in one of his roles returns true, else false
     * @author Gal Timea
     * @param user is a User object
     * @param permission is an enum which should be in the list of a role
     * @return true or false, depending on the fact that the user has that permission or not
     */
    public boolean hasPermission(User user, PermissionEnum permission) {
        for (Role role : user.getRoles()) {
            if (role.getPermissions().contains(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @author Tincu Ioana
     * @param permission
     * @return
     */
    public Optional<Role> findRoleWithPermission(PermissionEnum permission) {
        return roleRepository.findAll().stream()
                .filter(role -> role.getPermissions().contains(permission))
                .findFirst();
    }

    /**
     * @author Gal Timea
     * @param userId id of a User
     * @return a set of roles of a specific user
     */
    public Set<Role> getRoles(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.map(User::getRoles).orElse(null);
    }


    /**
     * @author Tincu Ioana
     * @param permission
     * @return
     */
    public List<User> getUsersWithPermission(PermissionEnum permission) {
        // Fetch all users
        List<User> allUsers = userRepository.findAll();

        // Filter users based on the permission and collect them into an ArrayList
        return allUsers.stream()
                .filter(user -> hasPermission(user, permission))
                .collect(Collectors.toList());
    }

}
