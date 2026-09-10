package com.lilamaris.lauth.identity.application.model.scope;

import com.lilamaris.lauth.identity.domain.scope.Action;
import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ScopeCodec {
    private static final String SEPARATOR = ".";

    public static ResourceScope decode(String scope) {
        StringPrecondition.requireNonBlank(scope, "scope");
        var parts = scope.split(Pattern.quote(SEPARATOR), -1);
        if (parts.length != 2) throw new IllegalStateException("Not valid scope format: " + scope);
        var resource = parts[0].trim();
        var actionStr = parts[1].trim();

        if (resource.isEmpty() || actionStr.isEmpty())
            throw new IllegalStateException("Resource or action part must not be empty. resource=" + resource + " action=" + actionStr);

        try {
            var action = Action.from(actionStr);
            return ResourceScope.of(resource, action);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Action is not defined. action=" + actionStr);
        }
    }

    public static Set<ResourceScope> decode(Collection<String> scope) {
        return scope.stream().map(ScopeCodec::decode).collect(Collectors.toUnmodifiableSet());
    }

    public static String encode(ResourceScope scope) {
        ObjectPrecondition.requireNonNull(scope, "scope");
        return scope.resource() + SEPARATOR + scope.action().canonicalName();
    }

    public static Set<String> encode(Collection<ResourceScope> scope) {
        return scope.stream().map(ScopeCodec::encode).collect(Collectors.toUnmodifiableSet());
    }
}
