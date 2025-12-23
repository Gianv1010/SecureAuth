package com.secureAuthLDC.secureAuthBE.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable()) //disabilita csfr
        .cors(Customizer.withDefaults()) //configura il cors
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //disabilita utilizzo sessioni
        .formLogin(form -> form.disable()) //disabilita form di login di spring
        .httpBasic(basic -> basic.disable())//disabilita autenticazione basica http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/**").permitAll()//utilizzato per dev, per monitorare l'app. Chiunque può accedervi
            .requestMatchers("/api/auth/**").permitAll()//chiunque può accedere agli endpoint sotto questo percorso
            .anyRequest().authenticated()//su tutti gli latri endopoint è necessario essere autenticati
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    // in dev: consenti il frontend locale
    config.setAllowedOrigins(List.of("https://localhost"));

    // metodi consentiti
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    // headers consentiti (Authorization ti servirà per JWT)
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

    // se userai cookie/sessioni (di solito con JWT no), metti true e NON usare "*"
    // config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }



}

