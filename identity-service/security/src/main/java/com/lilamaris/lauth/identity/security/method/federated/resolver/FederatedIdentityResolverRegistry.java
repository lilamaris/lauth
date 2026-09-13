package com.lilamaris.lauth.identity.security.method.federated.resolver;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FederatedIdentityResolverRegistry {
    private final Map<String, FederatedIdentityResolver> registry;

    public FederatedIdentityResolverRegistry(List<FederatedIdentityResolver> resolvers) {
        var registry = new HashMap<String, FederatedIdentityResolver>();

        for (var resolver : resolvers) {
            var key = keyOf(resolver);
            if (registry.putIfAbsent(key, resolver) != null)
                throw new IllegalArgumentException("Duplicated principal resolver key founded. key=" + key);
        }

        this.registry = Map.copyOf(registry);
    }

    public FederatedIdentity resolve(String registrationId, OAuth2User oAuth2User) {
        var resolver = registry.get(registrationId);
        if (resolver == null)
            throw new IllegalArgumentException("resolver matched with registration id not found. registrationId=" + registrationId);
        return resolver.resolve(oAuth2User);
    }

    private String keyOf(FederatedIdentityResolver resolver) {
        return resolver.registrationId();
    }
}
