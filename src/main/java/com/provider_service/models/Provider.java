package com.provider_service.models;

import java.util.Collection;
import java.util.Collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Document(collection = "providers")
public class Provider implements UserDetails{
	    @Id
	    private String id;
	    
		@Indexed(unique = true)
	    private String email;
	    
	    @JsonIgnore
	    private String password;
	    
	    private boolean enabled = true;
	    private boolean accountNonExpired = true;
	    private boolean accountNonLocked = true;
	    private boolean credentialsNonExpired = true;
	    
	    public Provider() {
			super();
		}

		public Provider(String email, String password) {
			super();
			this.email = email;
			this.password = password;
		}
	    
		// UserDetails implementation
	    @Override
	    public Collection<? extends GrantedAuthority> getAuthorities() {
	        return Collections.emptyList(); // No roles for now
	    }
	    
	    @Override
	    public String getUsername() {
	        return email;
	    }
	    
	    @Override
	    public boolean isAccountNonExpired() {
	        return accountNonExpired;
	    }
	    
	    @Override
	    public boolean isAccountNonLocked() {
	        return accountNonLocked;
	    }
	    
	    @Override
	    public boolean isCredentialsNonExpired() {
	        return credentialsNonExpired;
	    }
	    
	    @Override
	    public boolean isEnabled() {
	        return enabled;
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
	    
	    @Override
	    public String getPassword() {
	        return password;
	    }
	    
	    public void setPassword(String password) {
	        this.password = password;
	    }
	    
	    public void setEnabled(boolean enabled) {
	        this.enabled = enabled;
	    }
	    
	    public void setAccountNonExpired(boolean accountNonExpired) {
	        this.accountNonExpired = accountNonExpired;
	    }
	    
	    public void setAccountNonLocked(boolean accountNonLocked) {
	        this.accountNonLocked = accountNonLocked;
	    }
	    
	    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
	        this.credentialsNonExpired = credentialsNonExpired;
	    }
	    
}
