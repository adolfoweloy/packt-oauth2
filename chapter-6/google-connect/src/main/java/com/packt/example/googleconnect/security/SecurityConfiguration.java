package com.packt.example.googleconnect.security;

import com.packt.example.googleconnect.openid.OpenIdConnectFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private OpenIdConnectFilter openIdConnectFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .addFilterAfter(new OAuth2ClientContextFilter(), AbstractPreAuthenticatedProcessingFilter.class)
            .addFilterAfter(openIdConnectFilter, OAuth2ClientContextFilter.class)
            .authorizeRequests()
            .antMatchers("/").permitAll().and()
            .authorizeRequests()
            .antMatchers("/profile/*").authenticated().and()
            .authorizeRequests().anyRequest().authenticated().and()
            .httpBasic().authenticationEntryPoint(
                new LoginUrlAuthenticationEntryPoint("/callback")).and()
                .logout()
                .logoutSuccessUrl("/")
                .permitAll().and()
                .csrf().disable();
    }

}
