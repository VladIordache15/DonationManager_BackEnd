package demo.msg.javatraining.donationmanager.service;

import demo.msg.javatraining.donationmanager.config.security.JwtUtils;
import demo.msg.javatraining.donationmanager.persistence.model.RefreshToken;
import demo.msg.javatraining.donationmanager.persistence.repository.RefreshTokenRepository;
import demo.msg.javatraining.donationmanager.persistence.repository.UserRepository;
import demo.msg.javatraining.donationmanager.service.userDetailsService.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {


    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public void deleteRefreshTokenForUser(Long userId) {
        refreshTokenRepository.deleteRefreshTokenFromUser(userId);
    }


    public void createRefreshToken(String uuid, Long userId) {
        RefreshToken rt = new RefreshToken();
        rt.setRefreshToken(uuid);
        rt.setExpiryDate(Instant.now().plusSeconds(84000));
        rt.setUser(userRepository.findById(userId).get());
        refreshTokenRepository.save(rt);
    }


    public String exchangeRefreshToken(String refreshToken) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findById(refreshToken);
        if(refreshTokenOptional.isEmpty()) {
            throw new RuntimeException("Refresh token is not valid");
        }
        RefreshToken rt = refreshTokenOptional.get();
        if(rt.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(rt);
            throw new RuntimeException("Refresh token is expired");
        }
        return jwtUtils.generateJwtToken(userDetailsService.loadUserByUsername(rt.getUser().getUsername()), userRepository.getByUsername(rt.getUser().getUsername()));
    }
}