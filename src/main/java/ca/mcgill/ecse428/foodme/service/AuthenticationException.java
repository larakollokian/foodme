package ca.mcgill.ecse428.foodme.service;

public class AuthenticationException extends Exception {

	private static final long serialVersionUID = 357193968068712195L;

	public AuthenticationException(String errorMessage) {
		super(errorMessage);
	}

	public AuthenticationException(String errorMessage, Exception e) {
		super(errorMessage, e);
	}

}