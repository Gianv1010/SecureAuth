package com.secureAuthLDC.secureAuthBE.controller;

import com.secureAuthLDC.secureAuthBE.dto.RegisterScript;
import com.secureAuthLDC.secureAuthBE.entity.User;
import com.secureAuthLDC.secureAuthBE.repository.UserRepository;
import com.secureAuthLDC.secureAuthBE.service.AuthService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.secureAuthLDC.secureAuthBE.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.secureAuthLDC.secureAuthBE.dto.ForgotPasswordRequest;
import com.secureAuthLDC.secureAuthBE.dto.GenericResponse;
import com.secureAuthLDC.secureAuthBE.dto.ResetPasswordRequest;
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
    
    @PostMapping("/2fa/recovery/verify")
    public ResponseEntity<RegisterResponse> verifyRecovery(@RequestBody VerifyRecoveryCodeRequest req) {

        RegisterResponse res = authService.verifyRecoveryCode(req.getEmail(), req.getRecoveryCode());

        if (res.isSuccess()) {
            return ResponseEntity.ok(res);
        }

        return ResponseEntity.status(401).body(res);
    }

        @PostMapping("/forgot")
        public ResponseEntity<GenericResponse> forgot(@RequestBody ForgotPasswordRequest req) {
            // In dev: il tuo frontend sta su https://localhost
            // In produzione: mettilo in application.properties
            String appBaseUrl = "https://localhost";

            GenericResponse res = authService.forgotPassword(req.getEmail(), appBaseUrl);

            return ResponseEntity.status(201).body(res);
        }

        @PostMapping("/reset")
        public ResponseEntity<GenericResponse> reset(@RequestBody ResetPasswordRequest req) {

            GenericResponse res = authService.resetPassword(req.getToken(), req.getNewPassword(), req.getConfirmPassword());

            if (!res.isSuccess()) {
                return ResponseEntity.status(400).body(res);
            }
            return ResponseEntity.ok(res);
        }
    }

    	 

