package com.packt.example.googleuserinfo.openid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import java.util.Optional;

@Component
public class OpenIdConnectFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @Autowired
    private ClientTokenServices clientTokenServices;

    @Autowired
    private OAuth2ProtectedResourceDetails resourceDetails;

    @Autowired
    private UserRepository repository;

    @Autowired
    private OpenIDTokenExtractor openIDTokenExtractor;

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
    public Authentication attemptAuthentication(
        HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException, IOException, ServletException {

        try {
            OAuth2AccessToken accessToken = restTemplate.getAccessToken();

            clientTokenServices.saveAccessToken(resourceDetails, null, accessToken);
            OpenIDTokenExtractor.Claims claims = openIDTokenExtractor.extractFrom(accessToken);
            Optional<GoogleUser> found = repository.findByOpenIDSubject(claims.sub);

            if (found.isPresent()) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        found.get(), null, found.get().getAuthorities());

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
