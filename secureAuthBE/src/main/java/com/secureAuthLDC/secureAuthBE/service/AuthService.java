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

        
        //controllare se twoFactorEnabled = true => generare totpSecret e passarlo al costruttore User per creare l'oggetto
        //creo oggetto User
        
        //creo oggetto User
        //User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getTwoFactorEnabled());
        
        //user.setUsername(request.getUsername());
       // user.setEmail(request.getEmail());
       // user.setPasswordHash(passwordEncoder.encode(request.getPassword()));//capire come funziona bene questo!
        //user.setTwoFactorEnabled(request.getTwoFactorEnabled());
        
        //salvo l'oggetto User nel DB
        //userRepository.save(user);
        return "User registered successfully";
    }
}
