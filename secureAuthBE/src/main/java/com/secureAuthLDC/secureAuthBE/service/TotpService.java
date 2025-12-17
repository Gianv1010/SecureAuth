package com.secureAuthLDC.secureAuthBE.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

@Service
public class TotpService {
    private final GoogleAuthenticator ga = new GoogleAuthenticator();

    public String generaSecret() {
        GoogleAuthenticatorKey key = ga.createCredentials();
        return key.getKey(); // Base32
    }

    //metodo che serve per costuire una url che serve all'app google authenticator in questo caso per passare il secret
    public String costruisciOtpAuthUrl(String nomeApp, String email, String secret) {
        // formato standard per app Authenticator
        return "otpauth://totp/" + nomeApp + ":" + email +
               "?secret=" + secret + "&issuer=" + nomeApp;
    }
}
