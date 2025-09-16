package com.provider_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientDTO {
	private String id;
    private String email;
    private String accountStatus; // PENDING, ACTIVE, INACTIVE
    private LocalDateTime registrationDate;
    
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
    private boolean profileComplete;
    private LocalDateTime lastUpdated;
    
    // Constructors
    public PatientDTO() {}
    
    public PatientDTO(String id, String email, String accountStatus, LocalDateTime registrationDate) {
        this.id = id;
        this.email = email;
        this.accountStatus = accountStatus;
        this.registrationDate = registrationDate;
        this.profileComplete = false;
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
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getEmergencyContactName() {
        return emergencyContactName;
    }
    
    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }
    
    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }
    
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
    
    public String getBloodType() {
        return bloodType;
    }
    
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
    
    public String getAllergies() {
        return allergies;
    }
    
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
    
    public String getCurrentMedications() {
        return currentMedications;
    }
    
    public void setCurrentMedications(String currentMedications) {
        this.currentMedications = currentMedications;
    }
    
    public String getMedicalConditions() {
        return medicalConditions;
    }
    
    public void setMedicalConditions(String medicalConditions) {
        this.medicalConditions = medicalConditions;
    }
    
    public String getAssignedProviderId() {
        return assignedProviderId;
    }
    
    public void setAssignedProviderId(String assignedProviderId) {
        this.assignedProviderId = assignedProviderId;
    }
    
    public String getAssignedProviderName() {
        return assignedProviderName;
    }
    
    public void setAssignedProviderName(String assignedProviderName) {
        this.assignedProviderName = assignedProviderName;
    }
    
    public boolean isProfileComplete() {
        return profileComplete;
    }
    
    public void setProfileComplete(boolean profileComplete) {
        this.profileComplete = profileComplete;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    // Helper method
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return email;
    }
    
    // Check if required fields are filled
    public boolean hasRequiredInfo() {
        return firstName != null && lastName != null && phone != null && 
               emergencyContactName != null && emergencyContactPhone != null;
    }
}
