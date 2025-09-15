package com.provider_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.provider_service.models.Provider;
import com.provider_service.repository.ProviderRepository;
@Service
public class ProviderService implements UserDetailsService{
	@Autowired
	private ProviderRepository providerRepository ;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	    return providerRepository.findByEmail(email)
	            .orElseThrow(() -> new UsernameNotFoundException("Provider not found with email: " + email));
	}

	public Provider registerProvider(String email, String password) {
	    if (providerRepository.existsByEmail(email)) {
	        throw new RuntimeException("Provider already exists with email: " + email);
	    }

	    Provider provider = new Provider();
	    provider.setEmail(email);
	    provider.setPassword(passwordEncoder.encode(password));

	    return providerRepository.save(provider);
	}

	public Provider findByEmail(String email) {
	    return providerRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("Provider not found with email: " + email));
	}

}
