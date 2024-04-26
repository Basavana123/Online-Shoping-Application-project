package com.retail.ecom.service;

import org.springframework.http.ResponseEntity;

import com.retail.ecom.requestDTO.AuthRequest;
import com.retail.ecom.requestDTO.OtpRequest;
import com.retail.ecom.requestDTO.UserRequest;
import com.retail.ecom.responseDTO.AuthResponse;
import com.retail.ecom.responseDTO.UserResponse;
import com.retail.ecom.utility.ResponseStructure;
import com.retail.ecom.utility.SimpleResponseStructure;

public interface AuthorService {

	ResponseEntity<SimpleResponseStructure> registerUser(UserRequest userRequest);

	ResponseEntity<ResponseStructure<UserResponse>> verifyOtp(OtpRequest otpRequest);

	ResponseEntity<ResponseStructure<AuthResponse>> userLogin(AuthRequest authRequest);

	ResponseEntity<SimpleResponseStructure> userLogout(String accessToken, String refreshToken);

}
