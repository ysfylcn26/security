package com.business.auth.security.service;

import com.business.auth.security.dto.AccountJwtDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {

  @Value("${security.jwt.secret-key}")
  private String secretKey;
  @Value("${security.jwt.expiration}")
  private long jwtExpiration;
  @Value("${security.jwt.refresh-token.expiration}")
  private long refreshExpiration;
  private final static String BEARER = "Bearer ";

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String generateToken(String accountEmail) {
    return generateToken(new HashMap<>(), accountEmail);
  }

  public String generateToken(Map<String, Object> extraClaims, String accountEmail) {
    return buildToken(extraClaims, accountEmail, jwtExpiration);
  }

  public String generateRefreshToken(String accountEmail) {
    return buildToken(new HashMap<>(), accountEmail, refreshExpiration);
  }

  private String buildToken(Map<String, Object> extraClaims, String accountEmail, long expiration) {
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(accountEmail)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  public boolean isTokenValid(String token, String accountEmail) {
    final String email = extractEmail(token);
    return (email.equals(accountEmail)) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public Optional<AccountJwtDto> getJwtDataFormHeader(HttpServletRequest request) {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null ||!authHeader.startsWith(BEARER)) {
      return Optional.empty();
    }
    final String refreshToken = authHeader.substring(7);
    final String email = extractEmail(refreshToken);
    return Optional.of(new AccountJwtDto(refreshToken, email));
  }
}
