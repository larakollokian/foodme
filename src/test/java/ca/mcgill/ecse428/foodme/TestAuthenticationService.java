package ca.mcgill.ecse428.foodme;

import ca.mcgill.ecse428.foodme.repository.*;
import ca.mcgill.ecse428.foodme.service.*;
import ca.mcgill.ecse428.foodme.model.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodmeApplication.class)
public class TestAuthenticationService {

//    @TestConfiguration
//    static class AuthenticationTestConfiguration {
//
//        @Bean
//        public AuthenticationService authService() {
//            return new AuthenticationService();
//        }
//    }

    @Autowired
    private AuthenticationService authentication;

    @MockBean
    private FoodmeRepository foodRepo;


	private AppUser user;

	private static final String USERNAME = "testUsername";
	private static final String FIRSTNAME = "John";
	private static final String LASTNAME="Doe";
	private static String EMAIL="johnDoe@hotmail.ca";
	private String PASSWORD = "HelloWorld123";


	@Before
	public void setMockOutput() throws InvalidInputException {
	    try {
            user = foodRepo.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        }
	    catch(Exception e){
	        throw new InvalidInputException("User not created.");
        }
	}

    @Test
    public void testLoginWithValidPassword(){
        assertEquals(1, foodRepo.getAllUsers().size());

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

    }

    @Test
    public void testLoginWithWrongPassword(){
	    String error ="";
        assertEquals(1, foodRepo.getAllUsers().size());

        String password = "Hello";

        try{
            authentication.login(user.getUsername(),password);
        }
        catch(AuthenticationException e){
            error += e;
        }

        assertEquals("Invalid login information!!!",error);
    }

    @Test
    public void testLogout() {
        assertEquals(1, foodRepo.getAllUsers().size());

        try {
            // First login to get the session
            String bobSession = authentication.login(user.getUsername(), "HelloWorld123");

            // Then logout to invalidate the session
            authentication.logout(bobSession);

            // Usage of the invalidated session should fail
            try {
                authentication.getUserBySession(bobSession);
                fail("Invalidated session, no exception thrown");
            } catch (InvalidSessionException e) {
                // Expected
            }
        } catch (AuthenticationException e) {
            fail(e.getMessage());
            return;
        }
    }

}

