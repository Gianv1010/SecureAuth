package com.secureAuthLDC.secureAuthBE.entity;

import java.time.Instant;

//import annotazioni JPA
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;


@Entity
@Table(name = "auth_challenges")
public class AuthChallenge {
@Id
@Column(length = 36)
private String id; // --> PK, questo è il challengeID

@ManyToOne(optional = false) // --> un utente puo avere  piu challengeID, (optionale=false) --> non può essere null
@JoinColumn(name = "user_id")
private User user;

@Enumerated(EnumType.STRING)
@Column(nullable = false)
private ChallengeType type; // SETUP_2FA / LOGIN_2FA

@Column(nullable = false)
private Instant expiresAt; // scadenza del challengeID

@Column(nullable = false)
private int attempts = 0; // numero di tentavi effettuati con quel challenge

@Column(nullable = false)
private int maxAttempts = 5; // limite massimo di tentativi con un challenge

@Column(nullable = false)
private boolean consumed = false; // indica se il challenge è stato usato o meno

private Instant consumedAt; // quando il challenge è stato consumato

public AuthChallenge() {}

public AuthChallenge(User user, ChallengeType type, Instant expiresAt, int maxAttempts) {
 //creo l' UUID (challenge) stringa di 36 caratteri generata CASUALMENTE, praticamente impossibile da indovinare
//questo viene utilizzato come id ed è per di piu non sequenziale, non continene NESSUNA informazione, ed è impossibile da indovinare
// un UUID è l'identificatore del challengeID, ma data la sua complessità e unicità viene utilizzato come valore del challengeID
 this.id = java.util.UUID.randomUUID().toString();
 this.user = user;
 this.type = type;
 this.expiresAt = expiresAt;
 this.maxAttempts = maxAttempts;
}

//getters/setters

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public User getUser() {
	return user;
}

public void setUser(User user) {
	this.user = user;
}

public ChallengeType getType() {
	return type;
}

public void setType(ChallengeType type) {
	this.type = type;
}

public Instant getExpiresAt() {
	return expiresAt;
}

public void setExpiresAt(Instant expiresAt) {
	this.expiresAt = expiresAt;
}

public int getAttempts() {
	return attempts;
}

public void setAttempts(int attempts) {
	this.attempts = attempts;
}

public int getMaxAttempts() {
	return maxAttempts;
}

public void setMaxAttempts(int maxAttempts) {
	this.maxAttempts = maxAttempts;
}

public boolean isConsumed() {
	return consumed;
}

public void setConsumed(boolean consumed) {
	this.consumed = consumed;
}

public Instant getConsumedAt() {
	return consumedAt;
}

public void setConsumedAt(Instant consumedAt) {
	this.consumedAt = consumedAt;
}

//metodi
//metodo per verificare se il challenge è scaduto
//restituisce true se l'istante attuale è dopo la data di scadenza(expiresAt)
public boolean isExpired() { 
	return Instant.now().isAfter(expiresAt); 
}

//metodo che aumenta il numero di tentativi di uno (posso anche impostarlo come setter)
public void incrementAttempts() { 
	attempts++; 
}


}
