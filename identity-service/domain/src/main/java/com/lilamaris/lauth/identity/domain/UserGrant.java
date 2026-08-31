package com.lilamaris.lauth.identity.domain;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_grant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGrant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "scope_id", nullable = false)
    private UUID scopeId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private UserGrant(UUID userId, UUID scopeId, Instant createdAt) {
        this.userId = ObjectPrecondition.requireNonNull(userId, "userId");
        this.scopeId = ObjectPrecondition.requireNonNull(scopeId, "scopeId");
        this.createdAt = ObjectPrecondition.requireNonNull(createdAt, "createdAt");
    }

    public static UserGrant of(UUID userId, UUID scopeId, Instant createdAt) {
        return new UserGrant(userId, scopeId, createdAt);
    }
}
