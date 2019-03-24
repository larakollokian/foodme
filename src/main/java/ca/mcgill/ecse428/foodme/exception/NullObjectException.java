package ca.mcgill.ecse428.foodme.exception;

/*
 * Class used to handle unexisting objects (null)
 *
 * */

public class NullObjectException extends Exception {

    /**
    * Constructor
    * @param errorMessage
    */
    public NullObjectException(String errorMessage) {
        super(errorMessage);
    }

}
