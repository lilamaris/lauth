package com.lilamaris.lauth.identity.security.method.federated.service;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.ResolveFederatedAccountUseCase;
import com.lilamaris.lauth.identity.application.port.in.command.ResolveFederatedAccountCommand;
import com.lilamaris.lauth.identity.security.method.federated.resolver.FederatedIdentityResolverRegistry;
import com.lilamaris.lauth.identity.security.method.federated.resolver.OidcFederatedUserPrincipal;
import com.lilamaris.lauth.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@NullMarked
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {
    private final ResolveFederatedAccountUseCase resolveFederatedAccountUseCase;
    private final FederatedIdentityResolverRegistry resolverRegistry;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        var oidcUser = super.loadUser(userRequest);

        var registrationId = userRequest.getClientRegistration().getRegistrationId();

        try {
            var identity = resolverRegistry.resolve(registrationId, oidcUser);

            var command = ResolveFederatedAccountCommand.of(identity.registrationId(), identity.providerUserId(), identity.providerUserNickname());
            var user = resolveFederatedAccountUseCase.resolve(command);

            return new OidcFederatedUserPrincipal(
                    oidcUser.getAuthorities(),
                    oidcUser.getIdToken(),
                    oidcUser.getUserInfo(),
                    user
            );
        } catch (ApplicationException e) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(
                            "federated_account_resolve_failed",
                            "Failed to resolve the local account.",
                            null
                    ),
                    e
            );
        }
    }
}
