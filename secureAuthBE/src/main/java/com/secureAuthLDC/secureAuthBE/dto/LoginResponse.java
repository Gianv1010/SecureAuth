package com.secureAuthLDC.secureAuthBE.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private boolean success;
    private String message;
    private boolean enable2FA;

    public LoginResponse() {}

    public LoginResponse(boolean success, String message, boolean enable2FA) {
        this.success = success;
        this.message = message;
        this.enable2FA = enable2FA;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isEnable2FA() { return enable2FA; }
    public void setEnable2FA(boolean enable2FA) { this.enable2FA = enable2FA; }
}
