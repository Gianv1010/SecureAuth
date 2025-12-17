package com.secureAuthLDC.secureAuthBE.repository;

import com.secureAuthLDC.secureAuthBE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
