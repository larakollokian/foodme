package ca.mcgill.ecse428.foodme.exception;

public class InvalidInputException extends Exception{

    public InvalidInputException (String errorMessage) {
        super (errorMessage);
    }

}
