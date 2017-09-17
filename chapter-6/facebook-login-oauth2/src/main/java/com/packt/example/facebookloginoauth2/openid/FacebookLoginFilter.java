package com.packt.example.facebookloginoauth2.openid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Component
public class FacebookLoginFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @Autowired
    private ClientTokenServices clientTokenServices;

    @Autowired
    private OAuth2ProtectedResourceDetails resourceDetails;

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserInfoService userInfoService;

    private ApplicationEventPublisher eventPublisher;

    public FacebookLoginFilter() {
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

            clientTokenServices.saveAccessToken(resourceDetails, null, accessToken);

            Map<String, String> userInfo = userInfoService.getUserInfoFor(accessToken);
            Optional<FacebookUser> facebookUser = repository.findByFacebookId(userInfo.get("id"));

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    facebookUser.get(), null, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));

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
            throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
        }

    }

}
