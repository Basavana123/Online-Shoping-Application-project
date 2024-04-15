package com.retail.ecom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.retail.ecom.requestDTO.UserRequest;
import com.retail.ecom.responseDTO.UserResponse;
import com.retail.ecom.service.AuthorService;
import com.retail.ecom.utility.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class AuthorController {
	
//	public ResponseEntity<T>E name() {
//		
//	}
	
	//private AuthorService authorService;
	
	
//	public ResponseEntity<String> registerUser(@RequestBody UserRequest userRequest){
//		//return authorService.registerUser(userRequest);
//	}

}
