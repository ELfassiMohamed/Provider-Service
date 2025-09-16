package com.provider_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.provider_service.models.Patient;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String>{
	// Find all pending patients
    List<Patient> findByAccountStatus(String accountStatus);
    
    // Find pending patients ordered by registration date
    List<Patient> findByAccountStatusOrderByRegistrationDateDesc(String accountStatus);
    
    // Find patients assigned to a specific provider
    List<Patient> findByAssignedProviderId(String providerId);
    
    // Find patient by email
    Optional<Patient> findByEmail(String email);
    
    // Check if patient exists
    boolean existsByEmail(String email);
    
    // Find patients with incomplete profiles
    List<Patient> findByProfileCompletedFalse();
    
    // Find patients registered within a date range
    @Query("{'registrationDate': {$gte: ?0, $lte: ?1}}")
    List<Patient> findByRegistrationDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find recently registered patients (last N days)
    @Query("{'registrationDate': {$gte: ?0}}")
    List<Patient> findRecentlyRegistered(LocalDateTime since);
    
    // Count pending patients
    long countByAccountStatus(String accountStatus);
    
    // Count patients assigned to a provider
    long countByAssignedProviderId(String providerId);
}
