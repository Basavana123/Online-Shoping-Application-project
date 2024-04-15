package com.retail.ecom.responseDTO;

import com.retail.ecom.enums.UserRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
	
private int userId;
	
	private String displayName;
    
	private String userName;
	
	private String email;
	
	private UserRole userRole;

}
