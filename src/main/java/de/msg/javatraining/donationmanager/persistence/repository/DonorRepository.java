package de.msg.javatraining.donationmanager.persistence.repository;

import de.msg.javatraining.donationmanager.persistence.donorModel.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DonorRepository extends JpaRepository<Donor, Long> {
    Optional<Donor> findById(Long id);
    @Override
    List<Donor> findAll();

    @Query("delete from Donor d where d.id=:id")
    @Modifying
    Optional<Donor> deleteDonatorById(Long id);

    @Query("select d from Donor d inner join Donation dd on dd.donor.id=d.id where dd.campaign.id = :campaignId")
    @Modifying
    List<Donor> findDonatorsByCampaignId(@Param("campaignId") Long id);

//    @Query("delete from Donator d where d.id=:id")
//    @Modifying
//    Optional<Donator> deleteDonatorById(Long id);
}
