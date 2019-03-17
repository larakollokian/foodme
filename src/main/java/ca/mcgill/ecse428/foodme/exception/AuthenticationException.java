package ca.mcgill.ecse428.foodme.exception;

public class AuthenticationException extends Exception {

	public AuthenticationException(String errorMessage) {
		super(errorMessage);
	}

}