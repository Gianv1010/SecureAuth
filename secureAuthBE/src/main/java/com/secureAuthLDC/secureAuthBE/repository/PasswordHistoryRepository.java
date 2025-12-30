package com.secureAuthLDC.secureAuthBE.repository;

import com.secureAuthLDC.secureAuthBE.entity.PasswordHistory;
import com.secureAuthLDC.secureAuthBE.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {

	//pageable mi permette di prendere gli ultimi N risultati della query
	//questa query cerca l'utente passato come parametro e ordina in ordine decrescente
    List<PasswordHistory> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    //conta il numero di righe associate ad un utente (nel caso si vogliano ottenere tutte le righe e non solo N)
    long countByUser(User user);
}
