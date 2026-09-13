package com.lilamaris.lauth.identity.security.method.federated.service;

import com.lilamaris.lauth.identity.application.port.in.ResolveFederatedAccountUseCase;
import com.lilamaris.lauth.identity.application.port.in.command.ResolveFederatedAccountCommand;
import com.lilamaris.lauth.identity.security.method.federated.resolver.FederatedIdentityResolverRegistry;
import com.lilamaris.lauth.identity.security.method.federated.resolver.OAuth2FederatedUserPrincipal;
import com.lilamaris.lauth.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;

@NullMarked
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final ResolveFederatedAccountUseCase resolveFederatedAccountUseCase;
    private final FederatedIdentityResolverRegistry resolverRegistry;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var oAuth2User = super.loadUser(userRequest);

        var nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        var registrationId = userRequest.getClientRegistration().getRegistrationId();

        try {
            var identity = resolverRegistry.resolve(registrationId, oAuth2User);

            var command = ResolveFederatedAccountCommand.of(identity.registrationId(), identity.providerUserId(), identity.providerUserNickname());
            var user = resolveFederatedAccountUseCase.resolve(command);

            return new OAuth2FederatedUserPrincipal(
                    oAuth2User.getAuthorities(),
                    oAuth2User.getAttributes(),
                    nameAttributeKey,
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
