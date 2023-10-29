package demo.msg.javatraining.donationmanager.persistence.model.user;

import demo.msg.javatraining.donationmanager.persistence.model.Role;
import demo.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data


@NoArgsConstructor
@Entity
@Table(
		name = "users",
		uniqueConstraints = { 
			@UniqueConstraint(columnNames = "username"),
			@UniqueConstraint(columnNames = "email") 
		})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)	//in principiu merge dar baza de date trebe sa fie goala
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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


	public User(String firstName, String lastName, String mobileNumber, String username, String email, String password, boolean active, boolean firstLogin, int retryCount, Set<Role> roles, Set<Campaign> campaigns) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.mobileNumber = mobileNumber;
		this.username = username;
		this.email = email;
		this.password = password;
		this.active = active;
		this.firstLogin = firstLogin;
		this.retryCount = retryCount;
		this.roles = roles;
		this.campaigns = campaigns;
	}

	public Set<Campaign> getCampaigns() {
		return campaigns;
	}

}
