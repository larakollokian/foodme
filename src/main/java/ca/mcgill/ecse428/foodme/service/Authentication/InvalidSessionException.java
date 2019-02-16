package ca.mcgill.ecse428.foodme.service.Authentication;

public class InvalidSessionException extends Exception {

	public InvalidSessionException() {
		super("Session has expired or is invalid.");
	}
}