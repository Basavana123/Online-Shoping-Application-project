package com.retail.ecom.requestDTO;

import com.retail.ecom.enums.UserRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
	
	private String name;
	private String email;
	private String password;
	private UserRole userRole;

}
