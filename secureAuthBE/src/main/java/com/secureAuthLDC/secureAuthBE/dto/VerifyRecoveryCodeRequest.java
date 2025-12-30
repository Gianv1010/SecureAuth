package com.secureAuthLDC.secureAuthBE.dto;

public class VerifyRecoveryCodeRequest {
    private String email;
    private String recoveryCode;
    
    // getter/setter
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRecoveryCode() {
		return recoveryCode;
	}
	public void setRecoveryCode(String recoveryCode) {
		this.recoveryCode = recoveryCode;
	}

 
    
}

