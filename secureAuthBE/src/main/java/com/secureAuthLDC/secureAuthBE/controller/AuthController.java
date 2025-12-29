package com.secureAuthLDC.secureAuthBE.controller;

import com.secureAuthLDC.secureAuthBE.dto.RegisterScript;
import com.secureAuthLDC.secureAuthBE.entity.User;
import com.secureAuthLDC.secureAuthBE.repository.UserRepository;
import com.secureAuthLDC.secureAuthBE.service.AuthService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.secureAuthLDC.secureAuthBE.dto.*;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterScript request) throws Exception { //@requestBody-->Prendi il body della richiesta HTTP, converti il JSON in un oggetto Java di questo tipo e passamelo come parametro
		try {
			RegisterResponse response = authService.register(request);
	        if (!response.isSuccess()) {
	            return ResponseEntity.badRequest().body(response);
	        }
	        return ResponseEntity.status(201).body(response);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        return ResponseEntity.status(500).body(new RegisterResponse(false, "Errore interno", false));			
	     }//qui request è un oggetto perchè grazie a @RequestBody il json viene convertito in un oggetto di tipo RegisterScript ed esegue i setter in automatico.
    }
    
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginScript request) {
        LoginResponse res = authService.login(request);
        if (!res.isSuccess()) {
            return ResponseEntity.status(401).body(res);
        }
        return ResponseEntity.ok(res);
    }

    
    @PostMapping("/2fa/verify")
    public ResponseEntity<RegisterResponse> verify2FA(@RequestBody Verify2FARequest request) {
        RegisterResponse res = authService.verify2FA(request);

        if (!res.isSuccess()) {
            // 401 per codice sbagliato
            return ResponseEntity.status(401).body(res);
        }
        return ResponseEntity.status(201).body(res);
    }
    
    @PostMapping("/2fa/recovery/generate")
    public ResponseEntity<?> generateRecoveryCodes(@RequestBody String email) {
    	UserRepository userRepository = null;
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("Utente non trovato");
        }

        List<String> codes = authService.generateRecoveryCodes(user);
        return ResponseEntity.ok(codes); // MOSTRALI UNA VOLTA SOLA
    }
    
    @PostMapping("/2fa/recovery/verify")
    public ResponseEntity<RegisterResponse> verifyRecovery(@RequestBody VerifyRecoveryCodeRequest req) {

        RegisterResponse res = authService.verifyRecoveryCode(req.getEmail(), req.getRecoveryCode());

        if (res.isSuccess()) {
            return ResponseEntity.ok(res);
        }

        return ResponseEntity.status(401).body(res);
    }


    	 
}
