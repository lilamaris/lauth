package com.lilamaris.lauth.kenel.web.response.error;

import java.net.URI;

public interface TypeUriResolver {
    URI resolve(ErrorDescriptor descriptor);
}
