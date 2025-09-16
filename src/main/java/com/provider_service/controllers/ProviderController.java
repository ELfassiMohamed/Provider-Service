package com.provider_service.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.provider_service.dto.PatientDTO;
import com.provider_service.models.Provider;
import com.provider_service.services.PatientManagementService;
import com.provider_service.services.PatientManagementService.PatientStats;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/provider")
@CrossOrigin(origins = "*")
public class ProviderController {
    
    @Autowired
    private PatientManagementService patientManagementService;
    
    // Get all pending patients (waiting for activation)
    @GetMapping("/pending-patients")
    public ResponseEntity<List<PatientDTO>> getPendingPatients() {
        try {
            List<PatientDTO> pendingPatients = patientManagementService.getPendingPatients();
            return ResponseEntity.ok(pendingPatients);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // Get patients assigned to current provider
    @GetMapping("/my-patients")
    public ResponseEntity<List<PatientDTO>> getMyPatients(Authentication authentication) {
        try {
            Provider provider = (Provider) authentication.getPrincipal();
            List<PatientDTO> myPatients = patientManagementService.getProviderPatients(provider.getId());
            return ResponseEntity.ok(myPatients);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // Get specific patient details
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientDetails(@PathVariable("patientId") String patientId) {
        try {
            Optional<PatientDTO> patient = patientManagementService.getPatientById(patientId);
            if (patient.isPresent()) {
                return ResponseEntity.ok(patient.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving patient: " + e.getMessage());
        }
    }
    
    // Update patient profile (provider fills missing information)
    @PutMapping("/patient/{patientId}/profile")
    public ResponseEntity<?> updatePatientProfile(
            @PathVariable String patientId,
            @Valid @RequestBody PatientDTO updatedInfo,
            Authentication authentication) {
        try {
            Provider provider = (Provider) authentication.getPrincipal();
            
            PatientDTO updatedPatient = patientManagementService.updatePatientProfile(
                patientId, 
                updatedInfo, 
                provider.getId(), 
                provider.getEmail() // Using email as provider name for now
            );
            
            return ResponseEntity.ok(updatedPatient);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating patient profile: " + e.getMessage());
        }
    }
    
    // Activate patient (Proceed Treatment button)
    @PostMapping("/patient/{patientId}/activate")
    public ResponseEntity<?> activatePatient(@PathVariable("patientId") String patientId, Authentication authentication) {
        try {
            Provider provider = (Provider) authentication.getPrincipal();
            
            PatientDTO activatedPatient = patientManagementService.activatePatient(
                patientId, 
                provider.getId(), 
                provider.getEmail() // Using email as provider name for now
            );
            
            return ResponseEntity.ok(activatedPatient);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error activating patient: " + e.getMessage());
        }
    }
    
    // Get patient statistics
    @GetMapping("/stats")
    public ResponseEntity<PatientStats> getPatientStats() {
        try {
            PatientStats stats = patientManagementService.getPatientStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // Get recently registered patients (last N days)
    @GetMapping("/recent-patients")
    public ResponseEntity<List<PatientDTO>> getRecentPatients(@RequestParam(defaultValue = "7") int days) {
        try {
            List<PatientDTO> recentPatients = patientManagementService.getRecentlyRegisteredPatients(days);
            return ResponseEntity.ok(recentPatients);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // Provider dashboard summary
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummary> getDashboard(Authentication authentication) {
        try {
            Provider provider = (Provider) authentication.getPrincipal();
            
            // Get statistics
            PatientStats stats = patientManagementService.getPatientStats();
            
            // Get recent patients
            List<PatientDTO> recentPatients = patientManagementService.getRecentlyRegisteredPatients(7);
            
            // Get provider's patients
            List<PatientDTO> myPatients = patientManagementService.getProviderPatients(provider.getId());
            
            DashboardSummary dashboard = new DashboardSummary(
                stats,
                recentPatients.size(),
                myPatients.size(),
                recentPatients.subList(0, Math.min(5, recentPatients.size())) // Last 5
            );
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    // Dashboard summary DTO
    public static class DashboardSummary {
        private PatientStats stats;
        private int recentRegistrations;
        private int myPatientsCount;
        private List<PatientDTO> recentPatients;
        
        public DashboardSummary(PatientStats stats, int recentRegistrations, int myPatientsCount, List<PatientDTO> recentPatients) {
            this.stats = stats;
            this.recentRegistrations = recentRegistrations;
            this.myPatientsCount = myPatientsCount;
            this.recentPatients = recentPatients;
        }
        
        // Getters and Setters
        public PatientStats getStats() { return stats; }
        public void setStats(PatientStats stats) { this.stats = stats; }
        
        public int getRecentRegistrations() { return recentRegistrations; }
        public void setRecentRegistrations(int recentRegistrations) { this.recentRegistrations = recentRegistrations; }
        
        public int getMyPatientsCount() { return myPatientsCount; }
        public void setMyPatientsCount(int myPatientsCount) { this.myPatientsCount = myPatientsCount; }
        
        public List<PatientDTO> getRecentPatients() { return recentPatients; }
        public void setRecentPatients(List<PatientDTO> recentPatients) { this.recentPatients = recentPatients; }
    }
}
