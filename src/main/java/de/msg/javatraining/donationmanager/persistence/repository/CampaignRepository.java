package de.msg.javatraining.donationmanager.persistence.repository;

import de.msg.javatraining.donationmanager.persistence.campaignModel.Campaign;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findAll();
    Campaign findCampaignByName(String name);

    Campaign findCampaignById(Long id);
}
