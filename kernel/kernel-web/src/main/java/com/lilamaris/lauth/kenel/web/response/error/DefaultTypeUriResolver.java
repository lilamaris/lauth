package com.lilamaris.lauth.kenel.web.response.error;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.ArrayList;

@RequiredArgsConstructor
public class DefaultTypeUriResolver implements TypeUriResolver {
    private final static String ERROR_PATH = "errors/";
    private final URI baseUrl;

    @Override
    public URI resolve(ErrorDescriptor descriptor) {
        ObjectPrecondition.requireNonNull(descriptor, "descriptor");
        var parts = new ArrayList<String>();
        var type = descriptor.type().canonicalName();
        descriptor.resourceName().filter(name -> !name.isBlank()).ifPresent(parts::add);
        parts.add(type);
        var additionalPath = String.join("/", parts);

        return baseUrl
                .resolve(ERROR_PATH)
                .resolve(additionalPath);
    }
}
