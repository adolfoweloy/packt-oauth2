package com.packt.example.socialgithub.github;

import org.springframework.boot.autoconfigure.social.SocialProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.social.github")
public class GitHubProperties extends SocialProperties {

}
