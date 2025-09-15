package com.provider_service.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.provider_service.dto.AuthRequest;
import com.provider_service.dto.AuthResponse;
import com.provider_service.dto.RegisterRequest;
import com.provider_service.models.Provider;
import com.provider_service.services.JwtService;
import com.provider_service.services.ProviderService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
	@Autowired
	private ProviderService providerService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
	    try {
	        Provider provider = providerService.registerProvider(request.getEmail(), request.getPassword());
	        String token = jwtService.generateToken(provider);

	        return ResponseEntity.ok(new AuthResponse(token, "Registration successful", provider.getEmail()));
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(new AuthResponse(null, "Registration failed: " + e.getMessage(), null));
	    }
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
	    try {
	        Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
	        );

	        Provider provider = (Provider) authentication.getPrincipal();
	        String token = jwtService.generateToken(provider);

	        return ResponseEntity.ok(new AuthResponse(token, "Login successful", provider.getEmail()));
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(new AuthResponse(null, "Invalid credentials", null));
	    }
	}

	@GetMapping("/profile")
	public ResponseEntity<Provider> getProfile(Authentication authentication) {
	    Provider provider = (Provider) authentication.getPrincipal();
	    return ResponseEntity.ok(provider);
	}

}
