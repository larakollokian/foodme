package ca.mcgill.ecse428.foodme.service;

public class InvalidSessionException extends Exception {

	private static final long serialVersionUID = -8864842408601238959L;

	public InvalidSessionException(String errorMessage) {
		super(errorMessage);
	}
}