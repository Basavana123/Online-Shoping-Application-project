package com.retail.ecom.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("serial")
@Getter
@AllArgsConstructor
public class UserIsNotLoginException extends RuntimeException {
	
	String message;

}
