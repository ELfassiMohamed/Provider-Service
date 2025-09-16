package com.provider_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class MessagePublisherService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessagePublisherService.class);
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final String EXCHANGE_NAME = "patient-care-exchange";
    
    // Publish patient activation message (Provider → Patient Service)
    public void publishPatientActivation(String patientId, String providerId, String providerName) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("patientId", patientId);
            message.put("providerId", providerId);
            message.put("providerName", providerName);
            message.put("status", "ACTIVE");
            message.put("activationDate", LocalDateTime.now());
            message.put("message", "Your account has been activated by " + providerName);
            
            String jsonMessage = objectMapper.writeValueAsString(message);
            
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, "patient.activation", jsonMessage);
            
            logger.info("Published patient activation message for patient: {} by provider: {}", patientId, providerName);
            
        } catch (Exception e) {
            logger.error("Error publishing patient activation message: {}", e.getMessage(), e);
        }
    }
    
    // Publish medical history update message (Provider → Patient Service)
    public void publishMedicalUpdate(String action, String patientId, String providerId, String providerName, Object medicalData) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("action", action); // CREATE or UPDATE
            message.put("patientId", patientId);
            message.put("providerId", providerId);
            message.put("providerName", providerName);
            message.put("data", medicalData);
            message.put("timestamp", LocalDateTime.now());
            
            String jsonMessage = objectMapper.writeValueAsString(message);
            
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, "medical.updates", jsonMessage);
            
            logger.info("Published medical update message for patient: {} by provider: {}", patientId, providerName);
            
        } catch (Exception e) {
            logger.error("Error publishing medical update message: {}", e.getMessage(), e);
        }
    }
    
    // Publish patient profile update (when provider updates patient info)
    public void publishPatientProfileUpdate(String patientId, String providerId, String providerName, Map<String, Object> updatedFields) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("action", "PROFILE_UPDATE");
            message.put("patientId", patientId);
            message.put("providerId", providerId);
            message.put("providerName", providerName);
            message.put("updatedFields", updatedFields);
            message.put("timestamp", LocalDateTime.now());
            
            String jsonMessage = objectMapper.writeValueAsString(message);
            
            // This could be a new routing key for profile updates
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, "patient.profile.update", jsonMessage);
            
            logger.info("Published patient profile update for patient: {} by provider: {}", patientId, providerName);
            
        } catch (Exception e) {
            logger.error("Error publishing patient profile update: {}", e.getMessage(), e);
        }
    }
}
