package com.packt.example.microsoftlogin.security;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.CommonContentTypes;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.authentication.AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.web.AuthorizationGrantTokenExchanger;
import org.springframework.security.oauth2.core.AccessToken;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.TokenResponseAttributes;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomAuthorizationCodeExchanger implements AuthorizationGrantTokenExchanger<AuthorizationCodeAuthenticationToken> {

    @Override
    public TokenResponseAttributes exchange(AuthorizationCodeAuthenticationToken authorizationCodeAuthenticationToken) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = authorizationCodeAuthenticationToken.getClientRegistration();

        AuthorizationCode authorizationCode = new AuthorizationCode(
                authorizationCodeAuthenticationToken.getAuthorizationCode());
        AuthorizationGrant authorizationCodeGrant = new AuthorizationCodeGrant(
                authorizationCode, URI.create(clientRegistration.getRedirectUri()));
        URI tokenUri = URI.create(clientRegistration.getProviderDetails().getTokenUri());

        // Set the credentials to authenticate the client at the token endpoint
        ClientID clientId = new ClientID(clientRegistration.getClientId());
        Secret clientSecret = new Secret(clientRegistration.getClientSecret());
        ClientAuthentication clientAuthentication;
        if (ClientAuthenticationMethod.POST.equals(clientRegistration.getClientAuthenticationMethod())) {
            clientAuthentication = new ClientSecretPost(clientId, clientSecret);
        } else {
            clientAuthentication = new ClientSecretBasic(clientId, clientSecret);
        }

        try {
            HTTPRequest httpRequest = createTokenRequest(
                    clientRegistration, authorizationCodeGrant,
                    tokenUri, clientAuthentication);

            TokenResponse tokenResponse = TokenResponse.parse(httpRequest.send());

            if (!tokenResponse.indicatesSuccess()) {
                OAuth2Error errorObject = new OAuth2Error("invalid_token_response");
                throw new OAuth2AuthenticationException(errorObject, "error");
            }

            return createTokenResponse((AccessTokenResponse) tokenResponse);

        } catch (Exception e) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token_response"), e);
        }
    }

    private HTTPRequest createTokenRequest(ClientRegistration clientRegistration,
                                           AuthorizationGrant authorizationCodeGrant, URI tokenUri,
                                           ClientAuthentication clientAuthentication) throws MalformedURLException {

        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.POST, tokenUri.toURL());
        httpRequest.setContentType(CommonContentTypes.APPLICATION_URLENCODED);
        clientAuthentication.applyTo(httpRequest);
        Map<String,String> params = httpRequest.getQueryParameters();
        params.putAll(authorizationCodeGrant.toParameters());
        if (clientRegistration.getScope() != null && !clientRegistration.getScope().isEmpty()) {
            params.put("scope", clientRegistration.getScope().stream().reduce((a, b) -> a + " " + b).get());
        }
        if (clientRegistration.getClientId() != null) {
            params.put("client_id", clientRegistration.getClientId());
        }

        // not a safe approach (should be used when appropriate)
        if (clientRegistration.getClientSecret() != null) {
            params.put("client_secret", clientRegistration.getClientSecret());
        }
        httpRequest.setQuery(URLUtils.serializeParameters(params));
        httpRequest.setAccept(MediaType.APPLICATION_JSON_VALUE);
        httpRequest.setConnectTimeout(30000);
        httpRequest.setReadTimeout(30000);
        return httpRequest;
    }

    private TokenResponseAttributes createTokenResponse(AccessTokenResponse tokenResponse) {
        AccessTokenResponse accessTokenResponse = tokenResponse;
        String accessToken = accessTokenResponse.getTokens().getAccessToken().getValue();
        AccessToken.TokenType accessTokenType = AccessToken.TokenType.BEARER;
        long expiresIn = accessTokenResponse.getTokens().getAccessToken().getLifetime();
        Set<String> scopes = Collections.emptySet();
        if (!CollectionUtils.isEmpty(accessTokenResponse.getTokens().getAccessToken().getScope())) {
            scopes = new HashSet<>(accessTokenResponse.getTokens().getAccessToken().getScope().toStringList());
        }

        Map<String, Object> additionalParameters = accessTokenResponse.getCustomParameters().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return TokenResponseAttributes.withToken(accessToken)
            .tokenType(accessTokenType)
            .expiresIn(expiresIn)
            .scopes(scopes)
            .additionalParameters(additionalParameters)
            .build();
    }

}
