package com.packt.example.socialauthcode.facebook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.social.FacebookProperties;
import org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.GenericConnectionStatusView;
import org.springframework.social.facebook.api.Facebook;

@Configuration
@EnableSocial
@EnableConfigurationProperties(FacebookProperties.class)
public class FacebookConfiguration extends SocialAutoConfigurerAdapter {

	@Autowired
	private EnhancedFacebookProperties properties;

	@Bean
	@ConditionalOnMissingBean(Facebook.class)
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public Facebook facebook(ConnectionRepository repository) {
		Connection<Facebook> connection = repository
				.findPrimaryConnection(Facebook.class);
		return connection != null ? connection.getApi() : null;
	}

	@Bean(name = { "connect/facebookConnect", "connect/facebookConnected" })
	@ConditionalOnProperty(prefix = "spring.social", name = "auto-connection-views")
	public GenericConnectionStatusView facebookConnectView() {
		return new GenericConnectionStatusView("facebook", "Facebook");
	}

	@Override
	protected ConnectionFactory<?> createConnectionFactory() {
		return new CustomFacebookConnectionFactory(this.properties.getAppId(),
				this.properties.getAppSecret(), this.properties.getApiVersion());
	}

}
