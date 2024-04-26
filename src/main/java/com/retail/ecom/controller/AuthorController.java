package com.retail.ecom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.retail.ecom.requestDTO.AuthRequest;
import com.retail.ecom.requestDTO.OtpRequest;
import com.retail.ecom.requestDTO.UserRequest;
import com.retail.ecom.responseDTO.AuthResponse;
import com.retail.ecom.responseDTO.UserResponse;
import com.retail.ecom.service.AuthorService;
import com.retail.ecom.utility.ResponseStructure;
import com.retail.ecom.utility.SimpleResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1")
//@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
public class AuthorController {
	
//	public ResponseEntity<T>E name() {
//		
//	}
	
	private AuthorService authorService;
	
	//private JWTService jwtService;
	
	
	@PostMapping("/register")
	public ResponseEntity<SimpleResponseStructure> registerUser(@RequestBody UserRequest userRequest){
		return authorService.registerUser(userRequest);
	}

	@PostMapping("/users-verify")
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(@RequestBody OtpRequest otpRequest)
	{
		return authorService.verifyOtp(otpRequest);
	}
	
//	@GetMapping("/test")
//	public String test() {
//		return jwtService.genrateAccessToken("dfvh");
//	}
	
	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> userLogin(@RequestBody AuthRequest authRequest)
	{
		return authorService.userLogin(authRequest);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<SimpleResponseStructure> userLogout(@CookieValue("at") String accessToken ,@CookieValue("rt") String refreshToken){
		return authorService.userLogout(accessToken,refreshToken);
	}
	
	
}
