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
@Table(name = "credential")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Credential {
    @Id
    @Column(insertable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private Credential(UUID userId, String email, String passwordHash, Instant createdAt, Instant updatedAt) {
        this.userId = ObjectPrecondition.requireNonNull(userId, "userId");
        this.email = StringPrecondition.requireNonBlank(email, "email");
        this.passwordHash = StringPrecondition.requireNonBlank(passwordHash, "passwordHash");
        this.createdAt = ObjectPrecondition.requireNonNull(createdAt, "createdAt");
        this.updatedAt = TimePrecondition.requireAfterOrEqual(updatedAt, createdAt, "updatedAt", "createdAt");
    }

    public static Credential of(UUID userId, String email, String passwordHash, Instant createdAt) {
        return new Credential(userId, email, passwordHash, createdAt, createdAt);
    }
}
