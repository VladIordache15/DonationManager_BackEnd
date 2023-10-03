package de.msg.javatraining.donationmanager.controller.role;

import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.service.roleService.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles-dialog")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/all")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }
}