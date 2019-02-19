package ca.mcgill.ecse428.foodme.repository;

public class InvalidInputException extends Throwable{

    public InvalidInputException (String errorMessage) {
        super (errorMessage);
    }
}
