package com.secureAuthLDC.secureAuthBE.service;

import com.secureAuthLDC.secureAuthBE.dto.RegisterResponse;
import com.secureAuthLDC.secureAuthBE.dto.RegisterScript;
import com.secureAuthLDC.secureAuthBE.entity.RecoveryCode;
import com.secureAuthLDC.secureAuthBE.entity.User;
import com.secureAuthLDC.secureAuthBE.repository.RecoveryCodeRepository;
import com.secureAuthLDC.secureAuthBE.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.secureAuthLDC.secureAuthBE.security.CryptoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import com.secureAuthLDC.secureAuthBE.dto.Verify2FARequest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    private final RecoveryCodeRepository recoveryCodeRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TotpService totpService;
    private String keySecret;//posso commentarlo in quanto non viene mai usata
    private CryptoService crypto;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, CryptoService crypto, RecoveryCodeRepository recoveryCodeRespository, RecoveryCodeRepository recoveryCodeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        totpService = new TotpService();
        this.crypto = crypto;
        this.recoveryCodeRepository = recoveryCodeRepository;
        
    }

    @Transactional(rollbackFor = Exception.class)
    public RegisterResponse register(RegisterScript request) throws Exception {
    	RegisterResponse response;
    	if(!request.getConfirmPassword().equals(request.getPassword())) {
    		return new RegisterResponse(false, "Le password non coincidono", false);
    		//return ResponseEntity.badRequest().body(Map.of("status", "400", "message", "Le password non coincidono"));
    	}
    	
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
    		return new RegisterResponse(false, "Questa email è già stata utilizzata", false);
        	//return ResponseEntity.badRequest().body(Map.of("status", "400", "message", "Questa email è già stata utilizzata"));

        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
    		return new RegisterResponse(false, "Questo username è già stato utilizzato", false);
        	//return ResponseEntity.badRequest().body(Map.of("status", "400", "message", "Questo username è già stato utilizzato"));
        }

    	User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()), false, "");
    	userRepository.save(user);

    	if(request.getEnable2FA()==true) {
    		String secret, uriSecret;
    		secret = totpService.generaSecret();
    		uriSecret = totpService.costruisciOtpAuthUrl("SecureAuth", request.getEmail(), secret);
    		
        	user.setEnable2FA(true);
        	user.setTotpSecret(crypto.encryptToBase64(secret, user.getId()));
        	userRepository.save(user);
    		return new RegisterResponse(true, "Utente registrato con successo con 2FA", true, secret, uriSecret);
            }
		return new RegisterResponse(true, "Utente registrato con successo", false);

    	//return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Utente registrato con successo"));
        }
    
    //metodo che verifica che il codice totp inserito è corretto
    public RegisterResponse verify2FA(Verify2FARequest req) {
    	boolean twoFA = true; //in questo metodo la 2FA è per forza attiva
    	
    	//eseguo una serie di controlli che in realtà vengono già gestiti in altre parti della web app, ma essendo questa l'ultima parte cruciale prima di accedere all'account, faccio gli ultimi controlli server side, cosi l'unico modo vero per accedere all'account per un hacker è modificare il codice sorgente... impossibile 	
    	
    	//controllo se la request per qualche motivo è vuota, oppure non c'è mail o il codice
        if (req == null || req.getEmail() == null || req.getCode() == null) {
            return new RegisterResponse(false, "Dati mancanti", twoFA);
        }

        //prendo email e codice, utilizzo il metodo trim della classe String per rimuovere spazi bianchi, se ci sono, all'inizio e alla fine delle stringhe oppure in mazzo
        String email = req.getEmail().trim();
        String codeStr = req.getCode().trim();

        //regex che controlla se il codice contiene esattamente 6 cifre. (controllo superfluo perchè avviene già frontend, ma lo rieseguo server-side per sicurezza assoluta)
        if (!codeStr.matches("^\\d{6}$")) {
            return new RegisterResponse(false, "Codice non valido", twoFA);
        }

        //prendo l'utente con quella mail dal DB e se per qualche motivo strano non dovesse esserci mi restituisce null
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return new RegisterResponse(false, "Utente non trovato", twoFA);
        }

        //effettuo altri controlli in cui verifico se la 2FA è attiva, se il totp non è null e controllo se il totp contiene spazzi, è vuota o caratteri whitespace
        if (!user.getEnable2FA() || user.getTotpSecret() == null || user.getTotpSecret().isBlank()) {
            return new RegisterResponse(false, "2FA non attiva per questo utente", twoFA);
        }

        // decifra la secret salvata nel DB
        final String secretPlain;
        try {
            secretPlain = crypto.decryptFromBase64(user.getTotpSecret(), user.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return new RegisterResponse(false, "Errore nel recupero della secret", twoFA);
        }

        //converto la stringa totp in un intero per poter passarlo a varifyCode e avviare la verifica del totp
        int code = Integer.parseInt(codeStr);
        boolean ok = totpService.verifyCode(secretPlain, code);

        if (!ok) {
            return new RegisterResponse(false, "Codice errato o scaduto", twoFA);
        }
        
        //genera recovery codes SOLO la prima volta
        //controllo se esiste una lista di recovery code per l'utente indicato
        if (!recoveryCodeRepository.existsByUser(user)) {
        	//nel caso non esista genero una lista di recovery code
            return new RegisterResponse(true, "Codice corretto, recovery code generati", twoFA, generateRecoveryCodes(user));
        }        
        return new RegisterResponse(true, "Codice corretto", twoFA);
    }
    
    public List<String> generateRecoveryCodes(User user) {

    	// elimina eventuali codici precedenti
        recoveryCodeRepository.deleteByUser(user);
        
        //utilizzo SecureRandom e non random perchè random è prevedibile, mentre SecureRandom
        //crea un generatore di numeri crittograficamente sicuro
        SecureRandom random = new SecureRandom();
        //lista che conterrà i recovery code leggibili
        List<String> plainCodes = new ArrayList<>();//utilizzo una lista perchè piu flessibile
        
        //genero 10 codici
        for (int i = 0; i < 10; i++) {
            String code = generateSingleRecoveryCode(random);
            String hash = passwordEncoder.encode(code); // cripto il codice

            RecoveryCode rc = new RecoveryCode(user, hash);
            recoveryCodeRepository.save(rc);

            //ioinserisco i recovery code nella lista da far visualizzare al FE una sola volta
            plainCodes.add(code);
        }

        return plainCodes; // SOLO da mostrare all’utente
    }
    private String generateSingleRecoveryCode(SecureRandom random) {
    	//utilizzo questo alfabeto per creare i codici, stesso alfabeto utilizzato da google, github, AWS
    	//permette di eliminare ogni ambiguità nella digitazione dei caratteri
    	String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    	//Utilizzo StringBuilder perchè piu veloce nella composizione delle stringhe
        StringBuilder sb = new StringBuilder();
        
        //genero il codice lungo 12 caratteri
        for (int i = 0; i < 12; i++) {
        	//genero un numero casuale, utilizzo il numero generato per prendere un carattere "a caso" dall'alfabeto creato prima
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
    
    public RegisterResponse verifyRecoveryCode(String email, String code) {

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return new RegisterResponse(false, "Utente non trovato", false);
        }

        List<RecoveryCode> codes = recoveryCodeRepository.findByUserAndUsedFalse(user);

        for (RecoveryCode rc : codes) {
            if (passwordEncoder.matches(code, rc.getCodeHash())) {

                rc.setUsed(true);
                rc.setUsedAt(Instant.now());
                recoveryCodeRepository.save(rc);

                return new RegisterResponse(true, "Recovery code valido", false);
            }
        }

        return new RegisterResponse(false, "Recovery code non valido", false);
    }


    }

