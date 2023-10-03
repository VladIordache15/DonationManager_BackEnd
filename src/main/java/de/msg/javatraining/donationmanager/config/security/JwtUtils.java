package de.msg.javatraining.donationmanager.config.security;

import de.msg.javatraining.donationmanager.persistence.model.user.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${security.jwtSecret}")
    private String jwtSecret;

    @Value("${security.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(UserDetails userPrincipal, User user) {
        return generateTokenFromUsernameWithRights(userPrincipal.getUsername(), user);
    }

    public List<String> getPermissionsFromUser(User user) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream()
                        .map(Enum::toString)
                )
                .collect(Collectors.toList());
    }

    public String generateTokenFromUsernameWithRights(String username, User user) {
      List<String> permissions = getPermissionsFromUser(user);

        return Jwts.builder().setSubject(username).claim("permissions", permissions).claim("id", user.getId()).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
