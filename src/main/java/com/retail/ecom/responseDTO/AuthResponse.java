package com.retail.ecom.responseDTO;

import com.retail.ecom.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
	
	private int userId;
	private String username;
	private UserRole userRole;
	private long accessExpiration;
	private long refreshExpiration;
}
