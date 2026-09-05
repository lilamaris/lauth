package com.lilamaris.lauth.identity.application.model.scope;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.cozyr.kernel.core.condition.StringPrecondition;
import com.lilamaris.lauth.identity.domain.scope.Action;
import com.lilamaris.lauth.identity.domain.scope.Scope;

public record ResourceScope(
        String resource,
        Action action
) {
    public ResourceScope {
        resource = StringPrecondition.requireNonBlank(resource, "resource").trim();
        ObjectPrecondition.requireNonNull(action, "action");
    }

    public static ResourceScope of(String resource, Action action) {
        return new ResourceScope(resource, action);
    }

    public static ResourceScope from(Scope scope) {
        return new ResourceScope(scope.getResource(), scope.getAction());
    }
}
