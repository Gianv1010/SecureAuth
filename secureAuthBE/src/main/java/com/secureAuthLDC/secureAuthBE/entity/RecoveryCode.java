package com.secureAuthLDC.secureAuthBE.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity 	
@Table(name = "recovery_codes")
public class RecoveryCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//genera autonomamente la PK
    private Long id; //PK
    //ManyToOne--> indica che ci sono piu recovery code per un untente
    //optionale=false serve per dire che un recovery code deve avere un utente associato
    @ManyToOne(optional = false)
    // FK
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false)// non sono accettati valori null
    private String codeHash;

    @Column(nullable = false)
    private boolean used = false;
    
    //timestamp del momento in cui il codice è stato utilizzato
    private Instant usedAt;

    //salva la data di creazione del recovery code
    private Instant createdAt = Instant.now();

    public RecoveryCode() {}

    public RecoveryCode(User user, String codeHash) {
        this.user = user;
        this.codeHash = codeHash;
    }

    // getter & setter
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCodeHash() { return codeHash; }
    public void setCodeHash(String codeHash) { this.codeHash = codeHash; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public Instant getUsedAt() { return usedAt; }
    public void setUsedAt(Instant usedAt) { this.usedAt = usedAt; }
}
