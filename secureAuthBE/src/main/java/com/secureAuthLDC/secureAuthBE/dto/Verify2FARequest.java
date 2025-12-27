package com.secureAuthLDC.secureAuthBE.dto;

public class Verify2FARequest {
    private String email;
    private String code; // 6 cifre come stringa

    public Verify2FARequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
