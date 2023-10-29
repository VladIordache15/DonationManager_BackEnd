package demo.msg.javatraining.donationmanager.persistence.repository;

import demo.msg.javatraining.donationmanager.persistence.model.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Transactional
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    @Query("delete from RefreshToken where user.id = :id")
    @Modifying
    public void deleteRefreshTokenFromUser(Long id);
}
