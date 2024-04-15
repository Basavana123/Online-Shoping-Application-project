package com.retail.ecom.entity;

import com.retail.ecom.enums.UserRole;
import com.retail.ecom.responseDTO.UserResponse;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	
	private String displayName;
    
	private String userName;
	
	private String email;
	
	private String password;
	
	private UserRole userRole;
	
	private boolean emailVerified;
	
	
	
	// Lifecycle callback method to generate username before entity is persisted
    @PrePersist
    public void generateUsername() {
        // Extract username from email (assuming email format is username@example.com)
        String[] parts = email.split("@");
        if (parts.length > 0) {
            userName = parts[0];
        } else {
            // If email format is not as expected, you might handle it differently
            throw new IllegalArgumentException("Invalid email format");
        }
    }
	

}
