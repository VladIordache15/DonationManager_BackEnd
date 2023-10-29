package demo.msg.javatraining.donationmanager.persistence.repository;

import demo.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import demo.msg.javatraining.donationmanager.persistence.model.volManager.SignUps;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface SignUpsRepository extends JpaRepository<SignUps, Long> {


    SignUps findSignUpsById(Long id);

    List<SignUps> findSignUpsByEventJobsId(Long id);
}
