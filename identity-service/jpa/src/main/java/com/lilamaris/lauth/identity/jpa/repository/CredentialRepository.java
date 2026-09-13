package com.lilamaris.lauth.identity.jpa.repository;

import com.lilamaris.lauth.identity.application.model.credential.CredentialResetContext;
import com.lilamaris.lauth.identity.domain.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CredentialRepository extends JpaRepository<Credential, UUID> {
    boolean existsByEmail(String email);

    @Query("""
            SELECT
                c.userId AS userId,
                c.id AS credentialId
            FROM Credential c
            JOIN User u
                ON u.id = c.userId
            WHERE c.email = :email
            """)
    Optional<CredentialResetContext> findContextByEmail(@Param("email") String email);
}
