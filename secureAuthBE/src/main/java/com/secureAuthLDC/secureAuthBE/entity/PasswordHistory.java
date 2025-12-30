package com.secureAuthLDC.secureAuthBE.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "password_history",
    indexes = {
        @Index(name = "idx_ph_user_created", columnList = "user_id, createdAt")//rende piu veloce la query: "Dammi le ultime N password di questo utente ordinate per data"
    }
)
public class PasswordHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)// piu righe di passwordHistory appartengono ad un solo User
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100) //stringhe di massimo 100 cartateri (margine di sicurezza perchè Bcrypt crea stringhe di 60 caratteri)
    private String passwordHash;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public PasswordHistory() {}

    public PasswordHistory(User user, String passwordHash) {
        this.user = user;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }
}
