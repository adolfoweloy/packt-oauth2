package com.packt.example.googleconnect.openid;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2AuthenticationFailureEvent;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class OpenIdConnectFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private ClientTokenServices clientTokenServices;

    @Autowired
    private OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails;

    @Autowired
    private OpenIDAuthenticationRepository repository;

    private ApplicationEventPublisher eventPublisher;

    public OpenIdConnectFilter() {
        super("/callback");
        setAuthenticationManager(new NoopAuthenticationManager());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        super.setApplicationEventPublisher(eventPublisher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException, IOException, ServletException {

        try {
            OAuth2AccessToken accessToken = restTemplate.getAccessToken();
            Map<String, Object> tokenIdClaims = extractClaims(accessToken);
            Optional<OpenIDAuthentication> found = repository.findBySubject((String) tokenIdClaims.get("sub"));

            if (found.isPresent()) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        found.get().getUser(), null, found.get().getUser().getAuthorities());

                publish(new AuthenticationSuccessEvent(authentication));
                return authentication;
            } else {
                throw new UsernameNotFoundException("user not found");
            }
        } catch (OAuth2Exception e) {
            BadCredentialsException error = new BadCredentialsException(
                    "Cannot retrieve the access token", e);
            publish(new OAuth2AuthenticationFailureEvent(error));
            throw error;
        }
    }

    private Map<String, Object> extractClaims(OAuth2AccessToken accessToken) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            clientTokenServices.saveAccessToken(oAuth2ProtectedResourceDetails, authentication, accessToken);

            String idToken = accessToken.getAdditionalInformation().get("id_token").toString();
            Jwt decodedToken = JwtHelper.decode(idToken);
            return jsonMapper.readValue(decodedToken.getClaims(), Map.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void publish(ApplicationEvent event) {
        if (eventPublisher!=null) {
            eventPublisher.publishEvent(event);
        }
    }

    private static class NoopAuthenticationManager implements AuthenticationManager {

        @Override
        public Authentication authenticate(Authentication authentication)
                throws AuthenticationException {
            throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
        }

    }

}
