package com.packt.example.googleconnect.openid;

import com.packt.example.googleconnect.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpenIDAuthenticationRepository extends JpaRepository<OpenIDAuthentication, Long> {

    Optional<OpenIDAuthentication> findByUser(User user);

    Optional<OpenIDAuthentication> findBySubject(String subject);

}
