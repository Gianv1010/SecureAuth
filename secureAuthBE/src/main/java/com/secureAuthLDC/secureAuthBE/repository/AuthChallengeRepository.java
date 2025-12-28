package com.secureAuthLDC.secureAuthBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.secureAuthLDC.secureAuthBE.entity.AuthChallenge;

public interface AuthChallengeRepository extends JpaRepository<AuthChallenge, String> {
	
}
