package com.secureAuthLDC.secureAuthBE.service;

import com.secureAuthLDC.secureAuthBE.dto.RegisterScript;
import com.secureAuthLDC.secureAuthBE.entity.User;
import com.secureAuthLDC.secureAuthBE.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.secureAuthLDC.secureAuthBE.security.CryptoService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TotpService totpService;
   // private final TotpService totpService;
    private String keySecret;
    private CryptoService crypto;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, CryptoService crypto) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        totpService = new TotpService();
        this.crypto = crypto;
    }

    @Transactional(rollbackFor = Exception.class)
    public String register(RegisterScript request) throws Exception {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Questa email è già in uso";
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
        	return "Questo username è già in uso";
        }

    	User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()), false, "");
    	userRepository.save(user);

    	if(request.getEnable2FA()==true) {
        	user.setEnable2FA(true);
        	user.setTotpSecret(crypto.encryptToBase64(totpService.generaSecret(), user.getId()));
        	userRepository.save(user);
            //return "User registered successfully111";
        	//genera totpSecret
        	//keySecret = totpService.generaSecret();
        	//totpService.costruisciOtpAuthUrl(keySecret, request.getEmail(), "SecureAuth");
        	//creare keysecret e passarla al costruttore e impostare una variabile true da passare al costruttore sempre
        	
        }
        //else
        //{
        	//User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()), false, "");
        	//userRepository.save(user);
            return "User registered successfully222";
        	//User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getTwoFactorEnabled());
        }

        
        //salvo l'oggetto User nel DB
        
    }

