package demo.msg.javatraining.donationmanager.persistence.repository;

import demo.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import demo.msg.javatraining.donationmanager.persistence.model.volManager.EventJobs;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface EventJobsRepository extends JpaRepository<EventJobs, Long> {
    EventJobs findEventJobsById(Long id);

    List<EventJobs> findEventJobsByEventId(Long id);



}
