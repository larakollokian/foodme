package ca.mcgill.ecse428.foodme.exception;

/*
 * Class used to handle invalid inputs
 *
 * */

public class InvalidInputException extends Exception{

    /**
     * Constructor
     * @param errorMessage
     */
    public InvalidInputException (String errorMessage) {
        super (errorMessage);
    }

}
