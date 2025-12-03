package com.provider_service.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.provider_service.dto.AuthRequest;
import com.provider_service.dto.AuthResponse;
import com.provider_service.dto.ProfileCompletionRequest;
import com.provider_service.dto.ProviderProfileDTO;
import com.provider_service.dto.RegisterRequest;
import com.provider_service.models.Provider;
import com.provider_service.services.JwtService;
import com.provider_service.services.ProviderService;

import jakarta.validation.Valid;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Provider Authentication", description = "APIs d'authentification et gestion de profil des fournisseurs de soins")
public class AuthController {
    
    @Autowired
    private ProviderService providerService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    
    @Operation(
        summary = "Inscription d'un nouveau fournisseur",
        description = "Permet à un fournisseur de soins (médecin, infirmier, etc.) de créer un compte dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Inscription réussie - Token JWT généré",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erreur lors de l'inscription - Email déjà utilisé ou données invalides",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Informations d'inscription du fournisseur",
                required = true,
                content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            )
            @Valid @RequestBody RegisterRequest request) {
        try {
            Provider provider = providerService.registerProvider(request.getEmail(), request.getPassword());
            String token = jwtService.generateToken(provider);

            return ResponseEntity.ok(new AuthResponse(token, "Registration successful", provider.getEmail(), provider.getRole().getAuthority()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, "Registration failed: " + e.getMessage(), null));
        }
    }

    
    @Operation(
        summary = "Connexion d'un fournisseur",
        description = "Authentifie un fournisseur avec son email et mot de passe et retourne un token JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Connexion réussie - Token JWT valide généré",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Identifiants invalides - Email ou mot de passe incorrect",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Identifiants de connexion",
                required = true,
                content = @Content(schema = @Schema(implementation = AuthRequest.class))
            )
            @Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            Provider provider = (Provider) authentication.getPrincipal();
            String token = jwtService.generateToken(provider);

            return ResponseEntity.ok(new AuthResponse(token, "Login successful", provider.getEmail(), provider.getRole().getAuthority()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, "Invalid credentials", null));
        }
    }

    
    @Operation(
        summary = "Obtenir le profil du fournisseur",
        description = "Récupère les informations complètes du profil du fournisseur connecté",
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profil récupéré avec succès",
            content = @Content(schema = @Schema(implementation = Provider.class))
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
    @GetMapping("/profile")
    public ResponseEntity<Provider> getProfile(
            @Parameter(hidden = true) Authentication authentication) {
        Provider provider = (Provider) authentication.getPrincipal();
        return ResponseEntity.ok(provider);
    }

    
    @Operation(
        summary = "Compléter le profil du fournisseur",
        description = "Permet au fournisseur de compléter ses informations professionnelles (spécialité, licence, clinique, etc.)",
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profil complété avec succès",
            content = @Content(schema = @Schema(implementation = ProviderProfileDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Données invalides ou erreur lors de la mise à jour"
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
    @PutMapping("/complete-profile")
    public ResponseEntity<ProviderProfileDTO> completeProfile(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Informations professionnelles du fournisseur à compléter",
                required = true,
                content = @Content(schema = @Schema(implementation = ProfileCompletionRequest.class))
            )
            @RequestBody ProfileCompletionRequest profileUpdates,
            @Parameter(hidden = true) Authentication authentication) {
        try {
            Provider currentProvider = (Provider) authentication.getPrincipal();
            Provider updatedProvider = providerService.completeProviderProfile(currentProvider.getId(), profileUpdates);
            ProviderProfileDTO profileDTO = convertToProfileDTO(updatedProvider);
            return ResponseEntity.ok(profileDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    
    private ProviderProfileDTO convertToProfileDTO(Provider provider) {
        ProviderProfileDTO dto = new ProviderProfileDTO();
        dto.setProviderID(provider.getId());
        dto.setEmail(provider.getEmail());
        dto.setFullName(provider.getFullName());
        dto.setProfessionalTitle(provider.getProfessionalTitle());
        dto.setSpecialty(provider.getSpecialty());
        dto.setSubSpecialties(provider.getSubSpecialties());
        dto.setStateLicenses(provider.getStateLicenses());
        dto.setPrimaryClinicName(provider.getPrimaryClinicName());
        dto.setClinicAddress(provider.getClinicAddress());
        dto.setContactNumber(provider.getContactNumber());
        return dto;
    }
}
