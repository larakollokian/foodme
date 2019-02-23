package ca.mcgill.ecse428.foodme;

import ca.mcgill.ecse428.foodme.repository.*;
import ca.mcgill.ecse428.foodme.security.Password;
import ca.mcgill.ecse428.foodme.service.*;
import ca.mcgill.ecse428.foodme.model.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodmeApplication.class)
public class TestAuthenticationService {

    @Autowired
    private AuthenticationService authentication;

    @MockBean
    private FoodmeRepository foodRepo;


	private static final String USERNAME = "test";
	private static final String FIRSTNAME = "John";
	private static final String LASTNAME="Doe";
	private static String EMAIL="johnDoe@hotmail.ca";
	private String PASSWORD = "HelloWorld123";


	@Before
	public void setMockOutput() throws InvalidInputException {
	    try {
            AppUser user = new AppUser();
            user.setUsername(USERNAME);
            user.setLastName(LASTNAME);
            user.setFirstName(FIRSTNAME);
            user.setPassword(Password.getSaltedHash(PASSWORD));
            user.setEmail(EMAIL);

            Mockito.when(foodRepo.getNumberUsers()).thenReturn(1);
            Mockito.when(foodRepo.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD)).thenReturn(user);
            Mockito.when(foodRepo.getAppUser(USERNAME)).thenReturn(user);
            Mockito.when(foodRepo.getAppUser("none")).thenReturn(null);
        }
	    catch(Exception e){
	        e.printStackTrace();
        }
	}

    @Test
    public void testLoginWithValidPassword() throws InvalidInputException{

	    AppUser user;
	    try {
            user = foodRepo.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        }
	    catch(InvalidInputException e){
            throw new InvalidInputException("Invalid input format.");
        }

	    assertEquals(1, foodRepo.getNumberUsers());

	    String password = "HelloWorld123";
	    try{
	        //First login
	        String oldSession = authentication.login(user.getUsername(),password);

	        //Login twice, should be in a new session
	        String newSession = authentication.login(user.getUsername(),password);
	        assertNotEquals(oldSession,newSession);

	        //Ensure old session is invalid
            try {
                authentication.getUserBySession(oldSession);
                fail("Invalidated session, no exception thrown");
            } catch (InvalidSessionException e) {
                // Expected
            }

            assertEquals(user.getUsername(), authentication.getUserBySession(newSession).getUsername());

        }
        catch (AuthenticationException e) {
            fail("User login failed: "  + e.getMessage());
            return;
        } catch (InvalidSessionException e) {
            fail("User session invalid: "  + e.getMessage());
            return;
        }
	    catch (Exception e){
            fail(e.getMessage());
            return;
        }

    }
    @Test
    public void testLoginWithUnExistingUsername() throws InvalidInputException{
        String error ="";
        AppUser user;
        try {
            user = foodRepo.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        }
        catch(InvalidInputException e){
            throw new InvalidInputException("Invalid input format.");
        }

        assertEquals(1, foodRepo.getNumberUsers());

        String password = "none";
        String username ="none";

        try{
            authentication.login(username,password);
        }
        //Expected
        catch(InvalidSessionException e){
           error += e.getMessage();
        }
        catch(Exception e){
            fail(e.getMessage());
            return;
        }

        assertEquals("User does not exist",error);
    }
    @Test
    public void testLoginWithWrongPassword() throws InvalidInputException{
	    String error ="";
        AppUser user;
        try {
            user = foodRepo.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        }
        catch(InvalidInputException e){
            throw new InvalidInputException("Invalid input format.");
        }

        assertEquals(1, foodRepo.getNumberUsers());


        String password = "Hello";

        try{
            authentication.login(user.getUsername(),password);
        }
        //Expected
        catch(AuthenticationException e){
            error += e.getMessage();
        }
        catch(Exception e){
            fail(e.getMessage());
            return;
        }

        assertEquals("Invalid login password!!!",error);
    }

    @Test
    public void testLogout()throws InvalidInputException {
        AppUser user;
        try {
            user = foodRepo.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        }
        catch(InvalidInputException e){
            throw new InvalidInputException("Invalid input format.");
        }

        assertEquals(1, foodRepo.getNumberUsers());

        try {
            // First login to get the session
            authentication.login(user.getUsername(), "HelloWorld123");

            // Then logout to invalidate the session
            authentication.logout(user.getUsername());

            // Usage of the invalidated session should fail
            try {
                authentication.getUserBySession(user.getUsername());
                fail("Invalidated session, no exception thrown");
            } catch (InvalidSessionException e) {
                // Expected
            }
        } catch (AuthenticationException e) {
            fail(e.getMessage());
            return;
        }
        catch (InvalidSessionException e) {
            fail(e.getMessage());
            return;
        }
        catch (Exception e){
            fail(e.getMessage());
            return;
        }
    }

}

