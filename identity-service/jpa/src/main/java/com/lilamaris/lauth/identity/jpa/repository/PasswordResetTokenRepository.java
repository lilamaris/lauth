package com.lilamaris.lauth.identity.jpa.repository;

import com.lilamaris.lauth.identity.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
}
