package com.secureAuthLDC.secureAuthBE.controller;

import com.secureAuthLDC.secureAuthBE.dto.RegisterScript;
import com.secureAuthLDC.secureAuthBE.service.AuthService;
import org.springframework.web.bind.annotation.*;
import com.secureAuthLDC.secureAuthBE.dto.RegisterScript;
import com.secureAuthLDC.secureAuthBE.service.AuthService;
import org.springframework.web.bind.annotation.*;
import com.secureAuthLDC.secureAuthBE.dto.RegisterScript;
import com.secureAuthLDC.secureAuthBE.service.AuthService;
import org.springframework.web.bind.annotation.*;
import com.secureAuthLDC.secureAuthBE.dto.RegisterScript;
import com.secureAuthLDC.secureAuthBE.service.AuthService;
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
    public RegisterResponse register(@RequestBody RegisterScript request) { //@requestBody-->Prendi il body della richiesta HTTP, converti il JSON in un oggetto Java di questo tipo e passamelo come parametro
    	String msg;
		try {
			msg = authService.register(request);
	    	return new RegisterResponse(msg);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RegisterResponse("Errore durante la registrazione: " + e.getMessage());
		}//qui request è un oggetto perchè grazie a @RequestBody il json viene convertito in un oggetto di tipo RegisterScript ed esegue i setter in automatico.
    }
    	 
}
