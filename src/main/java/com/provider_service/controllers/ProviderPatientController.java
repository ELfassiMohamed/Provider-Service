package com.provider_service.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.provider_service.dto.PatientDTO;
import com.provider_service.services.ProviderPatientService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Provider Patient Management", description = "APIs de gestion des patients par les fournisseurs de soins")
@SecurityRequirement(name = "bearer-jwt")
public class ProviderPatientController {

    private final ProviderPatientService providerPatientService;

    
    @Operation(
        summary = "Obtenir la liste des patients",
        description = "Récupère la liste des patients associés au fournisseur connecté, filtrée par statut (PENDING, ACTIVE, INACTIVE, ou ALL)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste des patients récupérée avec succès",
            content = @Content(schema = @Schema(implementation = PatientDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non authentifié - Token JWT manquant ou invalide"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Accès refusé - Permissions insuffisantes"
        )
    })
    @GetMapping("/patients")
    public ResponseEntity<List<PatientDTO>> getPatients(
            @Parameter(
                description = "Statut des patients à récupérer (PENDING, ACTIVE, INACTIVE, ALL)",
                example = "PENDING"
            )
            @RequestParam(value = "status", defaultValue = "PENDING") String status,
            @Parameter(hidden = true) Authentication authentication) {

        String providerId = authentication.getName();
        List<PatientDTO> patients = providerPatientService.getPatients(providerId, status);

        return ResponseEntity.ok(patients);
    }

    
    @Operation(
        summary = "Obtenir les détails d'un patient spécifique",
        description = "Récupère les informations détaillées d'un patient par son ID. Le fournisseur doit avoir accès à ce patient."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Patient trouvé et informations récupérées",
            content = @Content(schema = @Schema(implementation = PatientDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Patient non trouvé ou le fournisseur n'a pas accès à ce patient"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non authentifié - Token JWT manquant ou invalide"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Accès refusé"
        )
    })
    @GetMapping("/patients/{patientId}")
    public ResponseEntity<PatientDTO> getPatient(
            @Parameter(
                description = "ID unique du patient",
                required = true,
                example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable(value = "patientId") String patientId,
            @Parameter(hidden = true) Authentication authentication) {

        String providerId = authentication.getName();

        List<PatientDTO> patients = providerPatientService.getPatients(providerId, "ALL");
        PatientDTO patient = patients.stream()
                .filter(p -> p.getId().equals(patientId))
                .findFirst()
                .orElse(null);

        return patient != null ?
                ResponseEntity.ok(patient) :
                ResponseEntity.notFound().build();
    }

    
    @Operation(
        summary = "Activer un compte patient",
        description = "Active le compte d'un patient en attente d'approbation. Cette action permet au patient d'accéder à ses dossiers médicaux."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Demande d'activation envoyée avec succès",
            content = @Content(schema = @Schema(example = "{\"message\": \"Patient activation request sent successfully\", \"patientId\": \"123\", \"status\": \"PROCESSING\"}"))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erreur interne lors du traitement de l'activation",
            content = @Content(schema = @Schema(example = "{\"error\": \"Failed to process activation request\"}"))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non authentifié"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Accès refusé - Le fournisseur n'est pas autorisé à activer ce patient"
        )
    })
    @PostMapping("/patients/{patientId}/activate")
    public ResponseEntity<Map<String, String>> activatePatient(
            @Parameter(
                description = "ID unique du patient à activer",
                required = true,
                example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable(value = "patientId") String patientId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Raison de l'activation (optionnel)",
                content = @Content(schema = @Schema(example = "{\"reason\": \"Vérification des documents complétée\"}"))
            )
            @RequestBody(required = false) Map<String, String> request,
            @Parameter(hidden = true) Authentication authentication) {

        String providerId = authentication.getName();
        String reason = request != null ?
                request.getOrDefault("reason", "Approved by provider") :
                "Approved by provider";

        try {
            providerPatientService.activatePatient(patientId, providerId, reason);

            return ResponseEntity.ok(Map.of(
                    "message", "Patient activation request sent successfully",
                    "patientId", patientId,
                    "status", "PROCESSING"
            ));
        } catch (Exception e) {
            log.error("Error activating patient {}: {}", patientId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to process activation request"));
        }
    }

    
    @Operation(
        summary = "Désactiver un compte patient",
        description = "Désactive le compte d'un patient actif. Cette action révoque l'accès du patient à ses dossiers médicaux."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Demande de désactivation envoyée avec succès",
            content = @Content(schema = @Schema(example = "{\"message\": \"Patient deactivation request sent successfully\", \"patientId\": \"123\", \"status\": \"PROCESSING\"}"))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erreur interne lors du traitement de la désactivation",
            content = @Content(schema = @Schema(example = "{\"error\": \"Failed to process deactivation request\"}"))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non authentifié"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Accès refusé - Le fournisseur n'est pas autorisé à désactiver ce patient"
        )
    })
    @PostMapping("/patients/{patientId}/deactivate")
    public ResponseEntity<Map<String, String>> deactivatePatient(
            @Parameter(
                description = "ID unique du patient à désactiver",
                required = true,
                example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable(value = "patientId") String patientId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Raison de la désactivation (optionnel)",
                content = @Content(schema = @Schema(example = "{\"reason\": \"Fin du traitement médical\"}"))
            )
            @RequestBody(required = false) Map<String, String> request,
            @Parameter(hidden = true) Authentication authentication) {

        String providerId = authentication.getName();
        String reason = request != null ?
                request.getOrDefault("reason", "Deactivated by provider") :
                "Deactivated by provider";

        try {
            providerPatientService.deactivatePatient(patientId, providerId, reason);

            return ResponseEntity.ok(Map.of(
                    "message", "Patient deactivation request sent successfully",
                    "patientId", patientId,
                    "status", "PROCESSING"
            ));
        } catch (Exception e) {
            log.error("Error deactivating patient {}: {}", patientId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to process deactivation request"));
        }
    }
}
