package com.secureAuthLDC.secureAuthBE.service;

import com.secureAuthLDC.secureAuthBE.dto.RegisterScript;
import com.secureAuthLDC.secureAuthBE.entity.User;
import com.secureAuthLDC.secureAuthBE.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
   // private final TotpService totpService;
    private String keySecret;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(RegisterScript request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Questa email è già in uso";
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
        	return "Questo username è già in uso";
        }

        if(request.getTwoFactorEnabled()) {
        	//genera totpSecret
        	//keySecret = totpService.generaSecret();
        	//totpService.costruisciOtpAuthUrl(keySecret, request.getEmail(), "SecureAuth");
        	
        }
        else
        {
        	//User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getTwoFactorEnabled());
        }

        
        //salvo l'oggetto User nel DB
        //userRepository.save(user);
        return "User registered successfully";
    }
}
