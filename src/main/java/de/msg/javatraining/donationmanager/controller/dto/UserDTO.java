package de.msg.javatraining.donationmanager.controller.dto;

import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String username;
    private String email;
    private String password;
    private Set<Role> roles = new HashSet<>();
    private Set<Campaign> campaigns = new HashSet<>();
    private boolean active;
    private boolean firstLogin;
    private int retryCount;
}
