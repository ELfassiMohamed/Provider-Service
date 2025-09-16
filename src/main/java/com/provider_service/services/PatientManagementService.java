package com.provider_service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.provider_service.dto.PatientDTO;
import com.provider_service.models.Patient;
import com.provider_service.repository.PatientRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientManagementService {
    
    private static final Logger logger = LoggerFactory.getLogger(PatientManagementService.class);
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private MessagePublisherService messagePublisherService;
    
    // Get all pending patients
    public List<PatientDTO> getPendingPatients() {
        List<Patient> pendingPatients = patientRepository.findByAccountStatusOrderByRegistrationDateDesc("PENDING");
        return pendingPatients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get all patients assigned to a provider
    public List<PatientDTO> getProviderPatients(String providerId) {
        List<Patient> providerPatients = patientRepository.findByAssignedProviderId(providerId);
        return providerPatients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get specific patient by ID
    public Optional<PatientDTO> getPatientById(String patientId) {
        Optional<Patient> patient = patientRepository.findById(patientId);
        return patient.map(this::convertToDTO);
    }
    
    // Add new patient (called when receiving RabbitMQ message)
    public Patient addNewPatient(String patientId, String email, String accountStatus, LocalDateTime registrationDate) {
        // Check if patient already exists
        if (patientRepository.existsById(patientId)) {
            logger.warn("Patient with ID {} already exists", patientId);
            return patientRepository.findById(patientId).orElse(null);
        }
        
        Patient patient = new Patient(patientId, email, accountStatus, registrationDate);
        Patient savedPatient = patientRepository.save(patient);
        
        logger.info("Added new patient: {} with status: {}", email, accountStatus);
        return savedPatient;
    }
    
    // Update patient profile (provider fills missing information)
    public PatientDTO updatePatientProfile(String patientId, PatientDTO updatedInfo, String providerId, String providerName) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        
        if (optionalPatient.isEmpty()) {
            throw new RuntimeException("Patient not found with ID: " + patientId);
        }
        
        Patient patient = optionalPatient.get();
        
        // Update personal information
        if (updatedInfo.getFirstName() != null) {
            patient.setFirstName(updatedInfo.getFirstName());
        }
        if (updatedInfo.getLastName() != null) {
            patient.setLastName(updatedInfo.getLastName());
        }
        if (updatedInfo.getPhone() != null) {
            patient.setPhone(updatedInfo.getPhone());
        }
        if (updatedInfo.getDateOfBirth() != null) {
            patient.setDateOfBirth(updatedInfo.getDateOfBirth());
        }
        if (updatedInfo.getGender() != null) {
            patient.setGender(updatedInfo.getGender());
        }
        
        // Update address information
        if (updatedInfo.getAddress() != null) {
            patient.setAddress(updatedInfo.getAddress());
        }
        if (updatedInfo.getCity() != null) {
            patient.setCity(updatedInfo.getCity());
        }
        if (updatedInfo.getState() != null) {
            patient.setState(updatedInfo.getState());
        }
        if (updatedInfo.getZipCode() != null) {
            patient.setZipCode(updatedInfo.getZipCode());
        }
        if (updatedInfo.getCountry() != null) {
            patient.setCountry(updatedInfo.getCountry());
        }
        
        // Update medical information
        if (updatedInfo.getEmergencyContactName() != null) {
            patient.setEmergencyContactName(updatedInfo.getEmergencyContactName());
        }
        if (updatedInfo.getEmergencyContactPhone() != null) {
            patient.setEmergencyContactPhone(updatedInfo.getEmergencyContactPhone());
        }
        if (updatedInfo.getBloodType() != null) {
            patient.setBloodType(updatedInfo.getBloodType());
        }
        if (updatedInfo.getAllergies() != null) {
            patient.setAllergies(updatedInfo.getAllergies());
        }
        if (updatedInfo.getCurrentMedications() != null) {
            patient.setCurrentMedications(updatedInfo.getCurrentMedications());
        }
        if (updatedInfo.getMedicalConditions() != null) {
            patient.setMedicalConditions(updatedInfo.getMedicalConditions());
        }
        
        // Assign provider
        patient.setAssignedProviderId(providerId);
        patient.setAssignedProviderName(providerName);
        
        // Update profile completion status
        patient.updateProfileCompletionStatus();
        
        Patient savedPatient = patientRepository.save(patient);
        logger.info("Updated patient profile for: {}", patient.getEmail());
        
        return convertToDTO(savedPatient);
    }
    
    // Activate patient (Proceed Treatment button)
    public PatientDTO activatePatient(String patientId, String providerId, String providerName) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        
        if (optionalPatient.isEmpty()) {
            throw new RuntimeException("Patient not found with ID: " + patientId);
        }
        
        Patient patient = optionalPatient.get();
        
        // Check if profile is complete
        if (!patient.hasRequiredInfo()) {
            throw new RuntimeException("Cannot activate patient: Profile is incomplete. Please fill required fields first.");
        }
        
        // Update local status
        patient.setAccountStatus("ACTIVE");
        patient.setActivated(true);
        patient.setAssignedProviderId(providerId);
        patient.setAssignedProviderName(providerName);
        
        Patient savedPatient = patientRepository.save(patient);
        
        // Send activation message to Patient Service via RabbitMQ
        messagePublisherService.publishPatientActivation(patientId, providerId, providerName);
        
        logger.info("Activated patient: {} by provider: {}", patient.getEmail(), providerName);
        return convertToDTO(savedPatient);
    }
    
    // Get patient statistics
    public PatientStats getPatientStats() {
        long pendingCount = patientRepository.countByAccountStatus("PENDING");
        long activeCount = patientRepository.countByAccountStatus("ACTIVE");
        long totalCount = patientRepository.count();
        
        return new PatientStats(pendingCount, activeCount, totalCount);
    }
    
    // Get recently registered patients (last 7 days)
    public List<PatientDTO> getRecentlyRegisteredPatients(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Patient> recentPatients = patientRepository.findRecentlyRegistered(since);
        
        return recentPatients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Convert Patient entity to DTO
    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setEmail(patient.getEmail());
        dto.setAccountStatus(patient.getAccountStatus());
        dto.setRegistrationDate(patient.getRegistrationDate());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setPhone(patient.getPhone());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setGender(patient.getGender());
        dto.setAddress(patient.getAddress());
        dto.setCity(patient.getCity());
        dto.setState(patient.getState());
        dto.setZipCode(patient.getZipCode());
        dto.setCountry(patient.getCountry());
        dto.setEmergencyContactName(patient.getEmergencyContactName());
        dto.setEmergencyContactPhone(patient.getEmergencyContactPhone());
        dto.setBloodType(patient.getBloodType());
        dto.setAllergies(patient.getAllergies());
        dto.setCurrentMedications(patient.getCurrentMedications());
        dto.setMedicalConditions(patient.getMedicalConditions());
        dto.setAssignedProviderId(patient.getAssignedProviderId());
        dto.setAssignedProviderName(patient.getAssignedProviderName());
        dto.setProfileComplete(patient.isProfileCompleted());
        dto.setLastUpdated(patient.getLastUpdated());
        
        return dto;
    }
    
    // Inner class for statistics
    public static class PatientStats {
        private long pendingCount;
        private long activeCount;
        private long totalCount;
        
        public PatientStats(long pendingCount, long activeCount, long totalCount) {
            this.pendingCount = pendingCount;
            this.activeCount = activeCount;
            this.totalCount = totalCount;
        }
        
        // Getters
        public long getPendingCount() { return pendingCount; }
        public long getActiveCount() { return activeCount; }
        public long getTotalCount() { return totalCount; }
        
        // Setters
        public void setPendingCount(long pendingCount) { this.pendingCount = pendingCount; }
        public void setActiveCount(long activeCount) { this.activeCount = activeCount; }
        public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    }
}
