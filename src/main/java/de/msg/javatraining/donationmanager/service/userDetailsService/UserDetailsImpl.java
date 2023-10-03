package de.msg.javatraining.donationmanager.service.userDetailsService;


import com.fasterxml.jackson.annotation.JsonIgnore;
import de.msg.javatraining.donationmanager.persistence.model.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;

	private String username;

	private String email;

	@JsonIgnore
	private String password;

	private boolean active;
	private boolean firstLogin;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(Long id, String username, String email, String password,
			boolean  active, boolean firstLogin,Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.active = active;
		this.authorities = authorities;
		this.firstLogin=firstLogin;
	}

	public static UserDetailsImpl build(User user) {
//		List<GrantedAuthority> authorities = user.getRoles().stream()
//				.map(role -> new SimpleGrantedAuthority(role.getPermissions().toString()))
//				.collect(Collectors.toList());
		List<GrantedAuthority> authorities = user.getRoles().stream()
				.flatMap(role -> role.getPermissions().stream()
						.map(permission -> new SimpleGrantedAuthority(permission.toString()))
				)
				.collect(Collectors.toList());

		return new UserDetailsImpl(
				user.getId(), 
				user.getUsername(), 
				user.getEmail(),
				user.getPassword(),
				user.isActive(),
				user.isFirstLogin(),
				authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return active;
	}

	public boolean isFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}
}
