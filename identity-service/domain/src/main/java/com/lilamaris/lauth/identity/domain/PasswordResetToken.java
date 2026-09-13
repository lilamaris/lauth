package com.lilamaris.lauth.identity.domain;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;
import com.lilamaris.lauth.kernel.core.condition.TimePrecondition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordResetToken {
    @Id
    private UUID id;

    @Column(name = "credential_id", nullable = false)
    private UUID credentialId;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    private PasswordResetToken(UUID id, UUID credentialId, String clientId, String tokenHash, Instant issuedAt, Instant expiresAt, Instant revokedAt, Instant consumedAt) {
        this.id = ObjectPrecondition.requireNonNull(id, "id");
        this.credentialId = ObjectPrecondition.requireNonNull(credentialId, "userId");
        this.clientId = StringPrecondition.requireNonBlank(clientId, "clientId");
        this.tokenHash = StringPrecondition.requireNonBlank(tokenHash, "tokenHash");
        this.issuedAt = ObjectPrecondition.requireNonNull(issuedAt, "issuedAt");
        this.expiresAt = TimePrecondition.requireAfterOrEqual(expiresAt, issuedAt, "expiresAt", "issuedAt");

        if (revokedAt != null) {
            this.revokedAt = TimePrecondition.requireAfterOrEqual(revokedAt, issuedAt, "revokedAt", "issuedAt");
        }
        if (consumedAt != null) {
            this.consumedAt = TimePrecondition.requireBetweenOrEqual(consumedAt, issuedAt, expiresAt, "consumedAt", "issuedAt", "expiresAt");
        }
    }

    public static PasswordResetToken of(UUID id, UUID credentialId, String clientId, String tokenHash, Instant issuedAt, Instant expiresAt) {
        return new PasswordResetToken(id, credentialId, clientId, tokenHash, issuedAt, expiresAt, null, null);
    }

    public boolean isAvailable(Instant now) {
        return revokedAt == null && consumedAt == null && now.isBefore(expiresAt);
    }
}
