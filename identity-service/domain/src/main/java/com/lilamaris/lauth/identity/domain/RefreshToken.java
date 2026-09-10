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
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @Column(insertable = false, updatable = false)
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    private RefreshToken(UUID sessionId, String tokenHash, Instant issuedAt, Instant expiresAt, Instant consumedAt) {
        this.sessionId = ObjectPrecondition.requireNonNull(sessionId, "sessionId");
        this.tokenHash = StringPrecondition.requireNonBlank(tokenHash, "tokenHash");
        this.issuedAt = ObjectPrecondition.requireNonNull(issuedAt, "issuedAt");
        this.expiresAt = TimePrecondition.requireAfterOrEqual(expiresAt, issuedAt, "expiresAt", "issuedAt");

        if (consumedAt != null) {
            this.consumedAt = TimePrecondition.requireBetweenOrEqual(consumedAt, issuedAt, expiresAt, "consumedAt", "issuedAt", "expiresAt");
        }
    }

    public static RefreshToken of(UUID sessionId, String tokenHash, Instant issuedAt, Instant expiresAt) {
        return new RefreshToken(sessionId, tokenHash, issuedAt, expiresAt, null);
    }
}
