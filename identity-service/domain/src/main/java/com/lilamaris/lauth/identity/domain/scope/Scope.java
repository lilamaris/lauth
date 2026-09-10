package com.lilamaris.lauth.identity.domain.scope;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "scope")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scope {
    @Id
    @Column(insertable = false, updatable = false)
    private UUID id;

    @Column(name = "resource", nullable = false)
    private String resource;

    @Convert(converter = ActionAttributeConverter.class)
    @Column(name = "action", nullable = false)
    private Action action;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private Scope(String resource, Action action, String description, Instant createdAt) {
        this.resource = StringPrecondition.requireNonBlank(resource, "resource");
        this.action = ObjectPrecondition.requireNonNull(action, "action");
        this.description = StringPrecondition.requireNonBlank(description, "description");
        this.createdAt = ObjectPrecondition.requireNonNull(createdAt, "createdAt");
    }

    public static Scope of(String resource, Action action, String description, Instant createdAt) {
        return new Scope(resource, action, description, createdAt);
    }
}
