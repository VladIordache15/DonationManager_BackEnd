package demo.msg.javatraining.donationmanager.persistence.repository;

import demo.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import demo.msg.javatraining.donationmanager.persistence.model.volManager.Event;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface EventRepository extends JpaRepository<Event, Long> {

    Event findEventById(Long id);
}
