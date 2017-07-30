package com.packt.example.clientauthorizationcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class ClientAuthorizationCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientAuthorizationCodeApplication.class, args);
    }

    @Configuration
    public class MvcConfiguration extends WebMvcConfigurerAdapter {

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("index");
        }
    }

}
