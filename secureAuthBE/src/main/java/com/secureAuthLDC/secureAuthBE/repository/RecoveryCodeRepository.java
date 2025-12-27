package com.secureAuthLDC.secureAuthBE.repository;

import com.secureAuthLDC.secureAuthBE.entity.RecoveryCode;
import com.secureAuthLDC.secureAuthBE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecoveryCodeRepository extends JpaRepository<RecoveryCode, Long> {

    List<RecoveryCode> findByUserAndUsedFalse(User user);

    void deleteByUser(User user);
    
    boolean existsByUser(User user);
}
