package com.secureAuthLDC.secureAuthBE.controller;

import com.secureAuthLDC.secureAuthBE.dto.RegisterScript;
import com.secureAuthLDC.secureAuthBE.service.AuthService;
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
			return authService.register(request);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return authService.register(request);
			}//qui request è un oggetto perchè grazie a @RequestBody il json viene convertito in un oggetto di tipo RegisterScript ed esegue i setter in automatico.
		
    }
    	 
}
