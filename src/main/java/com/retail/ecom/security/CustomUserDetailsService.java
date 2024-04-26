package com.retail.ecom.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.retail.ecom.Exception.UserNotFoundException;
import com.retail.ecom.repository.UserRespository;
import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

	private UserRespository userRespository;
	
	@Override
	public UserDetails loadUserByUsername(String username)  throws UsernameNotFoundException{
		
		System.out.println(username+"in customer detail service");
		return  userRespository.findByUserName(username). map(CustomerUserDetails::new)
				.orElseThrow(()-> new UserNotFoundException(" sdjgcsh User not found"));
	}

}
