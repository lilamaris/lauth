package com.lilamaris.lauth.kenel.web.response.error;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;

import java.util.ArrayList;
import java.util.List;

public class DefaultErrorCodeResolver implements ErrorCodeResolver {
    private final static String SEPARATOR = ".";

    @Override
    public String resolve(ErrorDescriptor descriptor) {
        ObjectPrecondition.requireNonNull(descriptor, "descriptor");

        var reason = descriptor.reason().getCanonicalName();
        var type = descriptor.type().canonicalName();
        var parts = new ArrayList<String>();

        descriptor.resourceName().filter(name -> !name.isBlank()).ifPresent(parts::add);
        parts.addAll(List.of(reason, type));

        return String.join(SEPARATOR, parts);
    }
}
