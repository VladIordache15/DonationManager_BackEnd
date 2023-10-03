package de.msg.javatraining.donationmanager.persistence.repository;

import de.msg.javatraining.donationmanager.persistence.model.ERole;
import de.msg.javatraining.donationmanager.persistence.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);

  // Fetch a role by its ID
  Optional<Role> findById(Integer id);

  // Save a role
  Role save(Role role);

  // Delete a role by its ID
  void deleteById(Integer id);

  // Find all roles
  @Override
  List<Role> findAll();

}
