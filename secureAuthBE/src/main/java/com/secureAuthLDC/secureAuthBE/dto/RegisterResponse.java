package com.secureAuthLDC.secureAuthBE.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterResponse {

    // sempre presenti
    private boolean success;
    private String message;
    private boolean enable2FA;

    // presenti SOLO se enable2FA = true
    private String secret;
    private String qrCodeUri;
    private List<String> recoveryCodes;


    // costruttore vuoto (obbligatorio per Jackson)
    public RegisterResponse() {}

    // senza 2FA
    public RegisterResponse(boolean success, String message, boolean enable2FA) {
        this.success = success;
        this.message = message;
        this.enable2FA = enable2FA;
    }

    // con 2FA
    public RegisterResponse(boolean success, String message, boolean enable2FA, String secret, String qrCodeUri) {
        this.success = success;
        this.message = message;
        this.enable2FA = enable2FA;
        this.secret = secret;
        this.qrCodeUri = qrCodeUri;
    }
    
    // con RecoveryCodes
    public RegisterResponse(boolean success, String message, boolean enable2FA, List<String> recoveryCodes) {
        this.success = success;
        this.message = message;
        this.enable2FA = enable2FA;
        this.recoveryCodes = recoveryCodes;
    }

    // ===== getters & setters =====

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isEnable2FA() {
        return enable2FA;
    }

    public void setEnable2FA(boolean enable2FA) {
        this.enable2FA = enable2FA;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getQrCodeUri() {
        return qrCodeUri;
    }

    public void setQrCodeUri(String qrCodeUri) {
        this.qrCodeUri = qrCodeUri;
    }
    public List<String> getRecoveryCodes() {
        return recoveryCodes;
    }

    public void setRecoveryCodes(String recoveryCode) {
        recoveryCodes.add(recoveryCode);
    }
}
