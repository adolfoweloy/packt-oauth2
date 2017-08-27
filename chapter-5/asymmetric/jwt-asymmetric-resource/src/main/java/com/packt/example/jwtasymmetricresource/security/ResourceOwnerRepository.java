package com.packt.example.jwtasymmetricresource.security;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface ResourceOwnerRepository extends CrudRepository<ResourceOwner, Long> {

    Optional<ResourceOwner> findByUsername(String username);

}
