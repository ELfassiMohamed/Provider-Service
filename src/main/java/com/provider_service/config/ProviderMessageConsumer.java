package com.provider_service.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.provider_service.services.PatientManagementService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ProviderMessageConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(ProviderMessageConsumer.class);
    
    @Autowired
    private PatientManagementService patientManagementService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Listen for patient registration messages from Patient Service
    @RabbitListener(queues = "${rabbitmq.queue.patient-registration:patient-registration-queue}")
    public void handlePatientRegistration(String message) {
        try {
            logger.info("Received patient registration message: {}", message);
            
            JsonNode jsonNode = objectMapper.readTree(message);
            String patientId = jsonNode.get("patientId").asText();
            String email = jsonNode.get("email").asText();
            String accountStatus = jsonNode.has("accountStatus") ? jsonNode.get("accountStatus").asText() : "PENDING";
            
            // Parse registration date
            LocalDateTime registrationDate;
            if (jsonNode.has("registrationDate")) {
                String dateStr = jsonNode.get("registrationDate").asText();
                registrationDate = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } else {
                registrationDate = LocalDateTime.now();
            }
            
            // Add patient to Provider Service database
            patientManagementService.addNewPatient(patientId, email, accountStatus, registrationDate);
            
            logger.info("Successfully processed patient registration for: {}", email);
            
        } catch (Exception e) {
            logger.error("Error processing patient registration message: {}", e.getMessage(), e);
        }
    }
}