package ca.mcgill.ecse428.foodme.service.Authentication;

public class AuthenticationException extends Exception {

	public AuthenticationException(String errorMessage) {
		super(errorMessage);
	}

	public AuthenticationException(String errorMessage, Exception e) {
		super(errorMessage, e);
	}

}