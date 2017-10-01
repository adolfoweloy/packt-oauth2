package example.packt.com.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class OAuth2Provider {

    @EnableAuthorizationServer
    public static class AuthorizationServer
        extends AuthorizationServerConfigurerAdapter {

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            //@formatter:off
            clients.inMemory()
                .withClient("clientapp")
                .secret("123456")
                .redirectUris(
                        "oauth2://profile/callback",
                        "http://localhost:9000/callback")
                .authorizedGrantTypes("authorization_code")
                .scopes("read_profile", "read_contacts");
            //@formatter:on
        }
    }

    @EnableResourceServer
    public static class ResourceServer
        extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            //@formatter:off
            http.authorizeRequests()
                .anyRequest()
                .authenticated()
            .and()
                .requestMatchers()
                .antMatchers("/api/**");
            //@formatter:on
        }

    }
}
