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
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2AuthenticationFailureEvent;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OpenIdConnectFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @Autowired
    private UserIdentity userIdentity;

    @Autowired
    private UserRepository repository;

    @Autowired
    private ObjectMapper jsonMapper;

    private ApplicationEventPublisher eventPublisher;

    public OpenIdConnectFilter() {
        super("/google/callback");
        setAuthenticationManager(new NoopAuthenticationManager());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        super.setApplicationEventPublisher(eventPublisher);
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException, IOException, ServletException {

        try {
            OAuth2AccessToken accessToken = restTemplate.getAccessToken();

            Claims claims = Claims.createFrom(jsonMapper, accessToken);
            GoogleUser googleUser = userIdentity.findOrCreateFrom(claims);
            repository.save(googleUser);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                googleUser, null, googleUser.getAuthorities());
            publish(new AuthenticationSuccessEvent(authentication));
            return authentication;
        } catch (OAuth2Exception e) {
            BadCredentialsException error = new BadCredentialsException(
                    "Cannot retrieve the access token", e);
            publish(new OAuth2AuthenticationFailureEvent(error));
            throw error;
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
            throw new UnsupportedOperationException(
                "No authentication should be done with this AuthenticationManager");
        }

    }

}
