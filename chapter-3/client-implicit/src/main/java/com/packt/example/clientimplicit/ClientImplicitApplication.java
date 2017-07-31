package com.packt.example.clientimplicit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class ClientImplicitApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientImplicitApplication.class, args);
    }

    @Configuration
    public class MvcConfiguration extends WebMvcConfigurerAdapter {

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("index");
        }
    }

}
