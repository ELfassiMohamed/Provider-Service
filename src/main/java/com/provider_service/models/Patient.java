package com.provider_service.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "patients_in_provider")
public class Patient {
    
    @Id
    private String id; // This will be the same ID as in Patient Service
    
    @Indexed(unique = true)
    private String email;
    
    private String accountStatus; // PENDING, ACTIVE, INACTIVE
    private LocalDateTime registrationDate;
    private LocalDateTime lastUpdated = LocalDateTime.now();
    
    // Personal Information
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    
    // Address Information
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    
    // Medical Information
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String bloodType;
    private String allergies;
    private String currentMedications;
    private String medicalConditions;
    
    // Provider Assignment
    private String assignedProviderId;
    private String assignedProviderName;
    
    // Status tracking
    private boolean profileCompleted = false;
    private boolean activated = false;
    
    // Constructors
    public Patient() {}
    
    public Patient(String id, String email, String accountStatus, LocalDateTime registrationDate) {
        this.id = id;
        this.email = email;
        this.accountStatus = accountStatus;
        this.registrationDate = registrationDate;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAccountStatus() {
        return accountStatus;
    }
    
    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getEmergencyContactName() {
        return emergencyContactName;
    }
    
    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }
    
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getBloodType() {
        return bloodType;
    }
    
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getAllergies() {
        return allergies;
    }
    
    public void setAllergies(String allergies) {
        this.allergies = allergies;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getCurrentMedications() {
        return currentMedications;
    }
    
    public void setCurrentMedications(String currentMedications) {
        this.currentMedications = currentMedications;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getMedicalConditions() {
        return medicalConditions;
    }
    
    public void setMedicalConditions(String medicalConditions) {
        this.medicalConditions = medicalConditions;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getAssignedProviderId() {
        return assignedProviderId;
    }
    
    public void setAssignedProviderId(String assignedProviderId) {
        this.assignedProviderId = assignedProviderId;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getAssignedProviderName() {
        return assignedProviderName;
    }
    
    public void setAssignedProviderName(String assignedProviderName) {
        this.assignedProviderName = assignedProviderName;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public boolean isProfileCompleted() {
        return profileCompleted;
    }
    
    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public boolean isActivated() {
        return activated;
    }
    
    public void setActivated(boolean activated) {
        this.activated = activated;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Helper methods
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return email;
    }
    
    public boolean hasRequiredInfo() {
        return firstName != null && lastName != null && phone != null && 
               emergencyContactName != null && emergencyContactPhone != null;
    }
    
    public void updateProfileCompletionStatus() {
        this.profileCompleted = hasRequiredInfo();
        this.lastUpdated = LocalDateTime.now();
    }
}