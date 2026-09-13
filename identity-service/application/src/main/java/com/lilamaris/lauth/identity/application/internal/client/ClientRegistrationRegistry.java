package com.lilamaris.lauth.identity.application.internal.client;

import com.lilamaris.lauth.identity.application.config.ClientRegistrationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientRegistrationRegistry {
    private final ClientRegistrationProperties properties;

    public ClientRegistrationProperties.RegisteredClient get(String clientId) {
        return properties.clients().get(clientId);
    }
}
