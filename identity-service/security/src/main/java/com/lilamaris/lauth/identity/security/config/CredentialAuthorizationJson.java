package com.lilamaris.lauth.identity.security.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lilamaris.lauth.identity.security.method.credential.request.CredentialAuthenticateToken;
import com.lilamaris.lauth.identity.security.method.federated.resolver.OAuth2FederatedUserPrincipal;
import com.lilamaris.lauth.identity.security.method.federated.resolver.OidcFederatedUserPrincipal;
import com.lilamaris.lauth.identity.security.principal.SerializableUserPrincipal;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.util.Collection;
import java.util.Map;

/** JSON mapping for authenticated principals stored in OAuth2 authorization attributes. */
final class CredentialAuthorizationJson {
    private CredentialAuthorizationJson() { }

    static JsonMapper mapper() {
        var allowedTypes = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(CredentialAuthenticateToken.class)
                .allowIfSubType(OAuth2FederatedUserPrincipal.class)
                .allowIfSubType(OidcFederatedUserPrincipal.class)
                .allowIfSubType(SerializableUserPrincipal.class);
        return JsonMapper.builder()
                .addModules(SecurityJacksonModules.getModules(CredentialAuthorizationJson.class.getClassLoader(), allowedTypes))
                .addMixIn(CredentialAuthenticateToken.class, CredentialTokenMixin.class)
                .addMixIn(OAuth2FederatedUserPrincipal.class, OAuth2PrincipalMixin.class)
                .addMixIn(OidcFederatedUserPrincipal.class, OidcPrincipalMixin.class)
                .addMixIn(SerializableUserPrincipal.class, PrincipalMixin.class)
                .build();
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    @JsonIgnoreProperties({"credentials", "authorities", "name", "authenticated"})
    abstract static class CredentialTokenMixin {
        @JsonCreator
        CredentialTokenMixin(@JsonProperty("principal") SerializableUserPrincipal principal) { }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    abstract static class PrincipalMixin { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    abstract static class OAuth2PrincipalMixin {
        @JsonCreator
        OAuth2PrincipalMixin(@JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities,
                             @JsonProperty("attributes") Map<String, Object> attributes,
                             @JsonProperty("nameAttributeKey") String nameAttributeKey,
                             @JsonProperty("userPrincipal") SerializableUserPrincipal userPrincipal) { }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
            getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonIgnoreProperties("attributes")
    abstract static class OidcPrincipalMixin {
        @JsonCreator
        OidcPrincipalMixin(@JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities,
                          @JsonProperty("idToken") OidcIdToken idToken,
                          @JsonProperty("userInfo") OidcUserInfo userInfo,
                          @JsonProperty("nameAttributeKey") String nameAttributeKey,
                          @JsonProperty("userPrincipal") SerializableUserPrincipal userPrincipal) { }
    }
}
