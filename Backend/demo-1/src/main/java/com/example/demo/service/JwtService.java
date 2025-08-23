package com.example.demo.service;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.*;

@Service
public class JwtService {
  private final Key key;
  private final int expirationMinutes;

  public JwtService(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expirationMinutes}") int expirationMinutes) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
    this.expirationMinutes = expirationMinutes;
  }

  public String generateToken(String subject, Map<String, Object> claims) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + expirationMinutes * 60_000L);
    return Jwts.builder()
        .setSubject(subject)
        .addClaims(claims)
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
  }
}
