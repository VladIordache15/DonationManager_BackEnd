package de.msg.javatraining.donationmanager.persistence.repository;

import de.msg.javatraining.donationmanager.persistence.model.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  Boolean existsByMobileNumber(String mobileNumber);

  User getByUsername(String username);
}
