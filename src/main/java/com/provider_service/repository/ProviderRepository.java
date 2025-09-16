package com.provider_service.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.provider_service.models.Provider;
@Repository
public interface ProviderRepository extends MongoRepository<Provider, String> {
	Optional<Provider> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
