package de.msg.javatraining.donationmanager.persistence.model.user;

import de.msg.javatraining.donationmanager.persistence.model.Role;
import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
		name = "users",
		uniqueConstraints = { 
			@UniqueConstraint(columnNames = "username"),
			@UniqueConstraint(columnNames = "email") 
		})
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String firstName;
	private String lastName;
	private String mobileNumber;
	private String username;
	private String email;
	private String password;
	private boolean active = true;
	private boolean firstLogin = true;
	private int retryCount = 0;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "user_role",
			joinColumns = @JoinColumn(name = "idUser"),
			inverseJoinColumns = @JoinColumn(name = "idRole")
	)
	private Set<Role> roles = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "user_campaign",
			joinColumns = @JoinColumn(name = "idUser"),
			inverseJoinColumns = @JoinColumn(name = "idCampaign")
	)
	private Set<Campaign> campaigns = new HashSet<>();

	public Set<Campaign> getCampaigns() {
		return campaigns;
	}

}
