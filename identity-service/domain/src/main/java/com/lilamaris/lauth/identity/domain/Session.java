package com.lilamaris.lauth.identity.domain;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.cozyr.kernel.core.condition.StringPrecondition;
import com.lilamaris.cozyr.kernel.core.condition.TimePrecondition;
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
@Table(name = "user_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session {
    @Id
    @Column(insertable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "device", nullable = false)
    private String device;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_used_at", nullable = false)
    private Instant lastUsedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    private Session(UUID userId, String device, Instant createdAt, Instant lastUsedAt, Instant expiresAt, Instant revokedAt) {
        this.userId = ObjectPrecondition.requireNonNull(userId, "userId");
        this.device = StringPrecondition.requireNonBlank(device, "device");
        this.createdAt = ObjectPrecondition.requireNonNull(createdAt, "createdAt");
        this.expiresAt = TimePrecondition.requireAfterOrEqual(expiresAt, createdAt, "expiresAt", "createdAt");

        Instant effectedAt = this.expiresAt;
        String effectedName = "expiresAt";
        if (revokedAt != null) {
            this.revokedAt = TimePrecondition.requireAfterOrEqual(revokedAt, createdAt, "revokedAt", "createdAt");
            effectedAt = revokedAt.isBefore(expiresAt) ? revokedAt : expiresAt;
            effectedName = revokedAt.isBefore(expiresAt) ? "revokedAt" : "expiresAt";
        }

        this.lastUsedAt = TimePrecondition.requireBetweenOrEqual(lastUsedAt, createdAt, effectedAt, "lastUsedAt", "createdAt", effectedName);
    }

    public static Session of(UUID userId, String device, Instant createdAt, Instant expiresAt) {
        return new Session(userId, device, createdAt, createdAt, expiresAt, null);
    }
}
