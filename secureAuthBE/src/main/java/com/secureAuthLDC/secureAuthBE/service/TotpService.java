package com.secureAuthLDC.secureAuthBE.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

@Service
public class TotpService {
    private final GoogleAuthenticator ga;

    //in questo costruttore gesisco la situazione in cui l'orario del cellulare dell'utente non è sincronizzato con quello del server
    public TotpService() {
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfigBuilder()
                .setWindowSize(3) 
                // 3 = accetta codice corrente + 1 finestra prima + 1 dopo (±1 step)
                // (windowSize è il numero di "time window" da controllare)
                .build();

        this.ga = new GoogleAuthenticator(config);
    }
    
    
    public String generaSecret() {
        GoogleAuthenticatorKey key = ga.createCredentials();
        return key.getKey(); // Base32
    }

    //metodo che serve per costuire una url che serve all'app google authenticator in questo caso per passare il secret
    public String costruisciOtpAuthUrl(String nomeApp, String email, String secret) {
        String encIssuer = URLEncoder.encode(nomeApp, StandardCharsets.UTF_8);
        String encEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);

        String label = encIssuer + ":" + encEmail;

        return "otpauth://totp/" + label
                + "?secret=" + secret
                + "&issuer=" + encIssuer;
    }
    
    // metodo per verificare il totp inserito da frontend
    public boolean verifyCode(String base32Secret, int code) {
        return ga.authorize(base32Secret, code);
    }
}
