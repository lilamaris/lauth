package com.lilamaris.lauth.identity.jpa.repository;

import com.lilamaris.lauth.identity.domain.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CredentialRepository extends JpaRepository<Credential, UUID> {
    boolean existsByEmail(String email);
}
