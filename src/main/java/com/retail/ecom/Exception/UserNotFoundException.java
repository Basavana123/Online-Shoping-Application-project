package com.retail.ecom.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


@SuppressWarnings("serial")
@Getter
@AllArgsConstructor
public class UserNotFoundException extends RuntimeException{
	
	private String message;

}
