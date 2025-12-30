package com.secureAuthLDC.secureAuthBE.entity;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "password_reset_tokens" /*nome della tabella*/, indexes = {
        @Index(name = "idx_prt_token_hash", columnList = "tokenHash"), /*otimizza la ricerca del token nel DB utilizzando una ricerca B tree, altrimenti sarebbe molto lento se dovesse ricercare per ogni riga*/
        @Index(name = "idx_prt_user_id", columnList = "user_id")/*Stessa ottimizazzione descritta prima ma per quanto riguarda user, cosi vado ad eliminare token vecchi già utilizzati*/
})
public class PasswordResetToken {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // NON salvare il token in chiaro: salva l'hash
    @Column(nullable = false, length = 128)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean consumed = false;

    private Instant consumedAt;

    public PasswordResetToken() {}

    public PasswordResetToken(User user, String tokenHash, Instant expiresAt) {
        this.id = java.util.UUID.randomUUID().toString();
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public boolean isConsumed() { return consumed; }
    public void setConsumed(boolean consumed) { this.consumed = consumed; }

    public Instant getConsumedAt() { return consumedAt; }
    public void setConsumedAt(Instant consumedAt) { this.consumedAt = consumedAt; }
    
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

}
