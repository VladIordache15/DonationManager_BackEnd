package demo.msg.javatraining.donationmanager.persistence.repository;

import demo.msg.javatraining.donationmanager.persistence.model.volManager.Volunteer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface VolunteerRepository extends JpaRepository<Volunteer,Long > {

    List<Volunteer> findAll();

    Boolean existsByUsername(String username);

    Optional<Volunteer> findByUsername(String username);
}
