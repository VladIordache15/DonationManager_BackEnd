package de.msg.javatraining.donationmanager.service.roleService;

import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    public List<Role> getAllRoles() {

        return roleRepository.findAll();
    }
}
