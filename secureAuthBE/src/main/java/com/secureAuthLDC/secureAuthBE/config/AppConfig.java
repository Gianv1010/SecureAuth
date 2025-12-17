package com.secureAuthLDC.secureAuthBE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration  // @Configuration -->Questa classe contiene metodi che creano oggetti usati come componenti dell’applicazione
				//è una classe di configurazione che viene eseguita all'avvio
public class AppConfig {

    @Bean//@Bean-->trasforma in oggetti che possono essere riutilizzati tramite dependency injection
    public PasswordEncoder passwordEncoder() { //PasswordEncoder è un'interfaccia definita da spring security e restituisce la funzione di criptazione BCryptPasswordEncoder (sempre fornita da spring security)
        return new BCryptPasswordEncoder();//esegue l'hasing della password
    }
}
	