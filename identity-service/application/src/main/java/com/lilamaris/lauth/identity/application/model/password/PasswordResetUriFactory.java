package com.lilamaris.lauth.identity.application.model.password;

import com.lilamaris.lauth.identity.application.model.opaque.OpaqueToken;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class PasswordResetUriFactory {
    public URI create(URI baseUri, OpaqueToken opaqueToken) {
        var token = OpaqueTokenCodec.encode(opaqueToken);
        return UriComponentsBuilder
                .fromUri(baseUri)
                .queryParam("token", token)
                .build()
                .encode()
                .toUri();
    }
}
