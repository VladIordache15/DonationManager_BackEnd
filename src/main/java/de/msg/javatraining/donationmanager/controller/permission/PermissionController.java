package de.msg.javatraining.donationmanager.controller.permission;
import de.msg.javatraining.donationmanager.exceptions.permission.PermissionException;
import de.msg.javatraining.donationmanager.persistence.model.PermissionEnum;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.service.permissionService.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController {


    @Autowired
    private PermissionService permissionService;


    @GetMapping("/all")
    public List<PermissionEnum> getAllPermissions(){

        return permissionService.getAllPermissions();
    }

    @GetMapping("/{roleId}/all")
    public List<PermissionEnum> getAllPermissions(@PathVariable Long roleId){
        return permissionService.getAllPermissions(roleId);
    }

    /**
     * @author Gal Timea
     * @param userId is the id of a user who wants to add a new permission
     * @param roleId is the id of a role which will be modified by adding a new permission to that role
     * @param permission is the new permission to add
     * @return the new modified role
     */
    @PostMapping("/{roleId}/{userId}/add")
    public ResponseEntity<?> addPermissionToRole(@PathVariable("userId") Long userId, @PathVariable("roleId") Integer roleId, @RequestBody PermissionEnum permission) {
        ResponseEntity<?> response;
        try{
            Role p = permissionService.addPermissionToRole(userId, roleId, permission);
            response = new ResponseEntity<>(p, HttpStatusCode.valueOf(200));
        }
        catch (PermissionException exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }

    /**
     * @author Gal Timea
     * @param userId is the id of a user who wants to delete a permission from a role
     * @param roleId is the id of a role which will be modified by deleting a permission from that role
     * @param permission is the permission to delete
     * @return the new modified role
     */
    @DeleteMapping("/{roleId}/{userId}/delete")
    public ResponseEntity<?> deletePermissionFromRole(@PathVariable("userId") Long userId, @PathVariable("roleId") Integer roleId, @RequestBody PermissionEnum permission) throws PermissionException {
        ResponseEntity<?> response;

        try{
            Role p = permissionService.deletePermissionFromRole(userId, roleId, permission);
            response = new ResponseEntity<>(p, HttpStatusCode.valueOf(200));
        }
        catch (PermissionException exception){
            response = new ResponseEntity<>(exception, HttpStatusCode.valueOf(200));
        }
        return response;
    }
}
