package com.provider_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.provider_service.config.RabbitConfig;
import com.provider_service.dto.PatientDTO;
import com.provider_service.dto.PatientStatusUpdateMessage;
import com.provider_service.dto.PatientSyncRequest;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderPatientService {

    private final RabbitTemplate rabbitTemplate;
    
    // In-memory cache for demo - in production, use Redis or database
    private final java.util.concurrent.ConcurrentHashMap<String, List<PatientDTO>> patientCache = 
            new java.util.concurrent.ConcurrentHashMap<>();

    public void activatePatient(String patientId, String providerId, String reason) {
        log.info("Provider {} activating patient {}", providerId, patientId);
        
        PatientStatusUpdateMessage message = new PatientStatusUpdateMessage(
                patientId, providerId, "ACTIVE", "PENDING");
        message.setReason(reason);
        
        rabbitTemplate.convertAndSend(
                RabbitConfig.PATIENT_EXCHANGE,
                RabbitConfig.PATIENT_SYNC_RESPONSE_ROUTING_KEY,
                message
        );
        
        log.info("Sent activation request for patient {}", patientId);
    }

    public void deactivatePatient(String patientId, String providerId, String reason) {
        log.info("Provider {} deactivating patient {}", providerId, patientId);
        
        PatientStatusUpdateMessage message = new PatientStatusUpdateMessage(
                patientId, providerId, "INACTIVE", "ACTIVE");
        message.setReason(reason);
        
        rabbitTemplate.convertAndSend(
                RabbitConfig.PATIENT_EXCHANGE,
                RabbitConfig.PATIENT_STATUS_ROUTING_KEY,
                message
        );
        
        log.info("Sent deactivation request for patient {}", patientId);
    }

    public List<PatientDTO> getPatients(String providerId, String status) {
        String requestId = UUID.randomUUID().toString();
        
        PatientSyncRequest request = new PatientSyncRequest(requestId, providerId, status);
        
        rabbitTemplate.convertAndSend(
                RabbitConfig.PATIENT_EXCHANGE,
                RabbitConfig.PATIENT_SYNC_ROUTING_KEY,
                request
        );
        
        log.info("Sent patient sync request for provider: {}", providerId);
        return List.of(); // Empty for now - will implement proper response later
    }

    
}