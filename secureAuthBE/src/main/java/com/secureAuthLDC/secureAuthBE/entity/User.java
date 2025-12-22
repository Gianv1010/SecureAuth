package com.secureAuthLDC.secureAuthBE.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity //dice che user è una tabella, ogni oggetto è una riga mentre ogni variabile è una colonna della tabella del DB
public class User {

    @Id //indica che id è la chiave primaria della tabella
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /*
     * @Generatedvalue permette di dire che il valore dell'id deve essere generato in automatico dal DB
     * strategy = GenerationType.IDENTITY --> indica come deve generare l'id (IDENTITY è la miglior soluzione per mysql, identity dice di sfruttare l'auto-increment dell'id)
     * */
    private Long id;

    private String username;
    private String email;
    private String passwordHash;
    private boolean twoFactorEnabled;
    private String totpSecret; //parola segreta per collegare il backend all'app di autenticazione

    //costruttore di default
    public User() {}
    
    public User(String username, String email, String passwordHash, boolean twoFactorEnabled, String totpSecret) {
    	this.username = username;
    	this.email = email;
    	this.passwordHash = passwordHash;
    	this.twoFactorEnabled = twoFactorEnabled;
    	this.totpSecret = totpSecret;
    }

    public Long getId() {
        return id;
    }
    
    public String getUsername() {
    	return username;
    }
    
    public void setUsername(String username) {
    	this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public String getTotpSecret() { 
        return totpSecret;
    }

    public void setTotpSecret(String totpSecret) { //gestire creazione totpSecret
        this.totpSecret = totpSecret;
    }
}
