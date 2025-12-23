package com.secureAuthLDC.secureAuthBE.service;

import com.secureAuthLDC.secureAuthBE.dto.RegisterResponse;
import com.secureAuthLDC.secureAuthBE.dto.RegisterScript;
import com.secureAuthLDC.secureAuthBE.entity.User;
import com.secureAuthLDC.secureAuthBE.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.secureAuthLDC.secureAuthBE.security.CryptoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import java.util.Map;
import org.springframework.http.ResponseEntity;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TotpService totpService;
    private String keySecret;//posso commentarlo in quanto non viene mai usata
    private CryptoService crypto;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, CryptoService crypto) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        totpService = new TotpService();
        this.crypto = crypto;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity register(RegisterScript request) throws Exception {
    	RegisterResponse response = new RegisterResponse();
    	if(!request.getConfirmPassword().equals(request.getPassword())) {
    		return ResponseEntity.badRequest().body(Map.of("status", "400", "message", "Le password non coincidono"));
    	}
    	
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
    		return ResponseEntity.badRequest().body(Map.of("status", "400", "message", "Questa email è già stata utilizzata"));

        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
    		return ResponseEntity.badRequest().body(Map.of("status", "400", "message", "Questo username è già stato utilizzato"));
        }

    	User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()), false, "");
    	userRepository.save(user);

    	if(request.getEnable2FA()==true) {
        	user.setEnable2FA(true);
        	user.setTotpSecret(crypto.encryptToBase64(totpService.generaSecret(), user.getId()));
        	userRepository.save(user);
            }
    	return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Utente registrato con successo"));
        }

    }

