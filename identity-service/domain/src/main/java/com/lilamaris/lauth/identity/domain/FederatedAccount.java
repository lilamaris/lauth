package com.lilamaris.lauth.identity.domain;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;
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
@Table(name = "federated_account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FederatedAccount {
    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "registration_id", nullable = false)
    private String registrationId;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private FederatedAccount(UUID userId, String registrationId, String providerUserId, Instant createdAt) {
        this.userId = ObjectPrecondition.requireNonNull(userId, "userId");
        this.registrationId = StringPrecondition.requireNonBlank(registrationId, "registrationId");
        this.providerUserId = StringPrecondition.requireNonBlank(providerUserId, "providerUserId");
        this.createdAt = ObjectPrecondition.requireNonNull(createdAt, "createdAt");
    }

    public static FederatedAccount of(UUID userId, String registrationId, String providerUserId, Instant createdAt) {
        return new FederatedAccount(userId, registrationId, providerUserId, createdAt);
    }
}
