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
@Table(name = "service_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    private UUID id;

    @Column(name = "handle")
    private String handle;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private User(UUID id, String displayName, Instant createdAt, Instant updatedAt) {
        this.id = ObjectPrecondition.requireNonNull(id, "id");
        this.displayName = StringPrecondition.requireNonBlank(displayName, "displayName");
        this.createdAt = ObjectPrecondition.requireNonNull(createdAt, "createdAt");
        this.updatedAt = TimePrecondition.requireAfterOrEqual(updatedAt, createdAt, "updatedAt", "createdAt");
    }

    public static User of(UUID id, String displayName, Instant createdAt) {
        return new User(id, displayName, createdAt, createdAt);
    }
}
