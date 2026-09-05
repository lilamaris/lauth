package com.lilamaris.lauth.identity.jdbc.row;

import com.lilamaris.lauth.identity.application.model.scope.ResourceScope;
import com.lilamaris.lauth.identity.domain.scope.Action;

import java.util.UUID;

public record ScopeRow(
        UUID id,
        String resource,
        String action
) {
    public ResourceScope toModel() {
        return ResourceScope.of(resource, Action.valueOf(action));
    }
}
