package demo.msg.javatraining.donationmanager.persistence.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ERole name;

	@ElementCollection(targetClass = PermissionEnum.class)
	@CollectionTable(
			name = "role_permission",
			joinColumns = @JoinColumn(name = "idRole"))
	@Enumerated(EnumType.STRING)
	@Fetch(FetchMode.JOIN)
	@Column(name = "Permission")
	private Set<PermissionEnum> permissions = new HashSet<>();

}