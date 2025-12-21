package com.secureAuthLDC.secureAuthBE.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

  private final SecretKey key;
  private final long expirationMinutes;

  public JwtService(
      @Value("${security.jwt.secret}") String secret, //prende la JWT secret dalle variabili d'ambiente
      @Value("${security.jwt.expiration-minutes}") long expirationMinutes //prende la durata del JWT
  ) {
	  //hmacShaKeyFor --> controlla che la key sia abbastanza lunga
	  //successivamente la key viene trasformata in byte
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMinutes = expirationMinutes;
  }

  //genera il token
  public String generateToken(String username) {
    Instant now = Instant.now();//istante di creazione del token
    Instant exp = now.plusSeconds(expirationMinutes * 60);//istante di scadenza del token

    //restituisce un token kwt
    return Jwts.builder()
        .subject(username)//inserisce username
        .issuedAt(Date.from(now))//data di creazione token
        .expiration(Date.from(exp))//data scadenza token
        .signWith(key)//chiave con cui è firmato il token
        .compact();//creazione url token
  }

  //estraee usernamen dal token con getSuject
  public String extractUsername(String token) {
    return parseClaims(token).getSubject();
  }

  //controlla se il token è valido, altrimenti resituisce false
  public boolean isTokenValid(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  //prende il token e controlla se è valido, se non è stato modificato, se non è scaduto e se è firmato con la sua key
  private Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(key) //prende la chiave con cui è firmato il token
        .build()
        .parseSignedClaims(token)//effettua tutti i controlli sul token
        .getPayload();//restituisce il payload ossia il claims
  }
}
