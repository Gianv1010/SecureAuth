package com.secureAuthLDC.secureAuthBE.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component //crea un istanza di questo filtro e la gestisce spring
public class JwtAuthenticationFilter extends OncePerRequestFilter { //estende la classe che permette di eseguire il filtro solo una volta su ogni richiesta http

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService; //serve per caricare l'utente dal DB

  public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization"); //prende il valore dall'header che ha come chiave Authorization
    if (authHeader == null || !authHeader.startsWith("Bearer ")) { //se authreader non ha authorization o non inizia con Bearer allora non fa nulla
      filterChain.doFilter(request, response); //passa la richiesta al prossimo filto di sprin security e se non esiste arriva al controller
      return;
    }

    //substring restituisce tutta la stringa authHeader ma dall' ottavo carattere in poi
    //sostanzialmente sto mettendo in token il token JWT
    String token = authHeader.substring(7);

    //controllo se il token è valido
    if (!jwtService.isTokenValid(token)) {
      filterChain.doFilter(request, response); //se non è valido non fa nulla e passa al prossimo filtro di spring secuirty
      return;
    }

    String username = jwtService.extractUsername(token);

    // Se già autenticato, non rifare
    //il securityContexHolder è una clase di Spring security che funge da punto di accesso per recuperare il securityContext corrente
    //il securityContext contiene tutte le informazioni dell'utente loggato cosi da sapere cosa può fare, quali autorizazzioni ecc
    //nell'if, quindi, si controlla se l'utente è già autenticato
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
    	//se l'utente è autenticato vengono prese tutte le sue informazioni del DB
      UserDetails userDetails = userDetailsService.loadUserByUsername(username); //da gestire, in quanto userDetails è solo un'interfaccia, bisogna creare una classe che la implementi (già "creata" con chatgpt, da controllare)
     /*sto lasciando da qui.*/

      
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }
}