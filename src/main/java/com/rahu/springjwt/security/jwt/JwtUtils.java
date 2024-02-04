package com.rahu.springjwt.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.rahu.springjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${rahu.app.jwtSecret}")
  private String jwtSecret;

  @Value("${rahu.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  public String generateToken(UserDetailsImpl userLoginDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("user_id", userLoginDetails.getId());
    claims.put("jwt_sign", userLoginDetails.getJwtSign());
    return doGenerateToken(claims, userLoginDetails.getUsername());
  }
  private String doGenerateToken(Map<String, Object> claims, String subject) {
    return "Bearer "
      + Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
      .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs * 1000L))
      .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }
  public Claims getAllClaimsFromToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
  }
  public Boolean validateToken(String token, UserDetails userLoginDetails) {
    final String email = getUserNameFromJwtToken(token);
    return (email.equals(userLoginDetails.getUsername()) && !isTokenExpired(token));
  }
  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }
  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }
  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
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
