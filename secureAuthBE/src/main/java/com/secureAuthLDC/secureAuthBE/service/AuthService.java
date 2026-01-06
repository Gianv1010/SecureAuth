package com.secureAuthLDC.secureAuthBE.service;

import com.secureAuthLDC.secureAuthBE.dto.RegisterResponse;
import com.secureAuthLDC.secureAuthBE.dto.RegisterScript;
import com.secureAuthLDC.secureAuthBE.entity.PasswordResetToken;
import com.secureAuthLDC.secureAuthBE.entity.RecoveryCode;
import com.secureAuthLDC.secureAuthBE.entity.User;
import com.secureAuthLDC.secureAuthBE.repository.PasswordHistoryRepository;
import com.secureAuthLDC.secureAuthBE.repository.PasswordResetTokenRepository;
import com.secureAuthLDC.secureAuthBE.repository.RecoveryCodeRepository;
import com.secureAuthLDC.secureAuthBE.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.secureAuthLDC.secureAuthBE.security.CryptoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import com.secureAuthLDC.secureAuthBE.dto.Verify2FARequest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.secureAuthLDC.secureAuthBE.dto.GenericResponse;
import com.secureAuthLDC.secureAuthBE.dto.LoginResponse;
import com.secureAuthLDC.secureAuthBE.dto.LoginScript;
import com.secureAuthLDC.secureAuthBE.entity.PasswordHistory;


@Service
public class AuthService {

    private final RecoveryCodeRepository recoveryCodeRepository;
    private final PasswordResetTokenRepository prtr;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TotpService totpService;
    private CryptoService crypto;
    private EmailService emailService;
    private final PasswordHistoryRepository passwordHistoryRepository;
    //@Value("${security.password.history_size:10}")
    private int passwordHistorySize = 10;//contiene il valore history-size=N=10 --> prende gli ultime 10 password già utilizzate

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, CryptoService crypto, RecoveryCodeRepository recoveryCodeRespository, RecoveryCodeRepository recoveryCodeRepository,PasswordResetTokenRepository prtr, EmailService emailService, PasswordHistoryRepository passwordHistoryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        totpService = new TotpService();
        this.crypto = crypto;
        this.recoveryCodeRepository = recoveryCodeRepository;
        this.prtr = prtr;
        this.emailService = emailService;
        this.passwordHistoryRepository = passwordHistoryRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public RegisterResponse register(RegisterScript request) throws Exception {
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
    // ******METODO PER LOGIN******
    public LoginResponse login(LoginScript req) {
        if (req == null || req.getEmail() == null || req.getPassword() == null) {
            return new LoginResponse(false, "Dati mancanti", false);
        }

        String email = req.getEmail().trim();
        String password = req.getPassword();

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return new LoginResponse(false, "Credenziali non valide", false);
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return new LoginResponse(false, "Credenziali non valide", false);
        }

        // se 2FA attiva -> FE deve andare alla pagina TOTP
        if (user.getEnable2FA()) {
            return new LoginResponse(true, "Inserisci il codice 2FA", true);
        }

        // login completato (qui in futuro emetterai token/sessione)
        return new LoginResponse(true, "Login completato", false);
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
            //PasswordEncoder utilizza sempre Bcrypt ma con saltin e cost factor automatici, COMUNQUE OTTIMO!
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

    @Transactional // serve per dire al backend che questo metodo è atomico o avviene tutto o niente
    public GenericResponse forgotPassword(String email, String appBaseUrl) {
        // Risposta sempre uguale (anti enumeration)
        GenericResponse generic = new GenericResponse(true,
            "Se l'email è registrata, ti abbiamo inviato un link per reimpostare la password.");

        if (email == null || email.isBlank()) return generic;
        
        //normalizzo l'email
        String normalizedEmail = email.trim().toLowerCase();
        //controllo se l'email esiste nel DB
        User user = userRepository.findByEmail(normalizedEmail).orElse(null);
        if (user == null) return generic;

        // invalida eventuali vecchi token
        prtr.deleteByUser(user);
        
        //genera token e lo crittografa
        String tokenPlain = generateResetToken();
        String tokenHash = sha256Base64Url(tokenPlain);
        
        //tempo di duarata del token, a partire da questo momento * 15 minuti
        java.time.Instant expiresAt = java.time.Instant.now().plus(15, java.time.temporal.ChronoUnit.MINUTES);

        PasswordResetToken token = new PasswordResetToken(user, tokenHash, expiresAt);
        prtr.save(token);
        
        //creo link da inviare tramite email
        String resetLink = appBaseUrl + "/resetPassword?token=" + tokenPlain;

        emailService.sendPasswordResetEmail(normalizedEmail, resetLink);

        return generic;
    }

    
    
    @Transactional
    public GenericResponse resetPassword(String tokenPlain, String newPassword, String confirmPassword) {
        if (tokenPlain == null || tokenPlain.isBlank()) {
            return new GenericResponse(false, "Token mancante o non valido.");
        }
        if (newPassword == null || confirmPassword == null || newPassword.isBlank()) {
            return new GenericResponse(false, "Password mancante.");
        }
        if (!newPassword.equals(confirmPassword)) {
            return new GenericResponse(false, "Le password non coincidono.");
        }
        
        
        String tokenHash = sha256Base64Url(tokenPlain.trim());

        PasswordResetToken prt = prtr.findByTokenHashAndConsumedFalse(tokenHash).orElse(null);

        if (prt == null) {
            return new GenericResponse(false, "Token non valido o già usato.");
        }
        if (prt.isExpired()) {
            return new GenericResponse(false, "Token scaduto. Richiedi un nuovo reset.");
        }

        User user = prt.getUser();
        // 1) confronto con password attuale
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            return new GenericResponse(false, "Utilizza una password che non hai mai usato.");

        }
        // 2) confronto con ultime N nello storico
        //var riconosce direttamente il tipo che si trova dop =
        var lastN = passwordHistoryRepository.findByUserOrderByCreatedAtDesc(
            user, PageRequest.of(0, passwordHistorySize)
        );

        for (PasswordHistory ph : lastN) {
            if (passwordEncoder.matches(newPassword, ph.getPasswordHash())) {
                return new GenericResponse(false, "Utilizza una password che non hai mai usato.");
            }
        }
        PasswordHistory passwordHistory = new PasswordHistory(user, user.getPasswordHash());
        passwordHistoryRepository.save(passwordHistory);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        prt.setConsumed(true);
        prt.setConsumedAt(java.time.Instant.now());
        prtr.save(prt);

        return new GenericResponse(true, "Password aggiornata con successo. Ora puoi fare login.");
    }

    
    
    
    private String generateResetToken() {
        byte[] bytes = new byte[32];
        new java.security.SecureRandom().nextBytes(bytes);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256Base64Url(String input) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(dig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    }

