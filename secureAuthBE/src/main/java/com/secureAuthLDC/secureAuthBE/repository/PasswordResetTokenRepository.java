package com.secureAuthLDC.secureAuthBE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.secureAuthLDC.secureAuthBE.entity.PasswordResetToken;
import com.secureAuthLDC.secureAuthBE.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

    Optional<PasswordResetToken> findByTokenHashAndConsumedFalse(String tokenHash);

    void deleteByUser(User user);
}
