package ca.mcgill.ecse428.foodme;

import ca.mcgill.ecse428.foodme.exception.AuthenticationException;
import ca.mcgill.ecse428.foodme.exception.InvalidInputException;
import ca.mcgill.ecse428.foodme.exception.NullObjectException;
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

/**
 * This class serves to test the AuthenticationService.java
 * */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodmeApplication.class)
public class AuthenticationServiceTests {

    @Autowired
    private AuthenticationService authentication;

    @MockBean
    private AppUserRepository userRepository;


    private static final String USERNAME = "test";
    private static final String FIRSTNAME = "John";
    private static final String LASTNAME="Doe";
    private static String EMAIL="johnDoe@hotmail.ca";
    private String PASSWORD = "HelloWorld123";


    /**
     * Initial setup
     * */
    @Before
    public void setMockOutput() {
        try {
            AppUser user = new AppUser();
            user.setUsername(USERNAME);
            user.setLastName(LASTNAME);
            user.setFirstName(FIRSTNAME);
            user.setPassword(Password.getSaltedHash(PASSWORD));
            user.setEmail(EMAIL);

            Mockito.when(userRepository.getNumberUsers()).thenReturn(1);
            Mockito.when(userRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD)).thenReturn(user);
            Mockito.when(userRepository.getAppUser(USERNAME)).thenReturn(user);
            Mockito.doThrow(new NullObjectException("User does not exist")).when(userRepository).getAppUser("none");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * UT successful login
     * */
    @Test
    public void testLoginWithValidPassword() {
        AppUser user = new AppUser();
        try {
             user = userRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        }
        catch(Exception e){
            //Not expected
        }

        assertEquals(1, userRepository.getNumberUsers());

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
            } catch (NullObjectException e) {
                // Expected
            }

            assertEquals(user.getUsername(), authentication.getUserBySession(newSession).getUsername());

        }
        catch (AuthenticationException e) {
            fail("User login failed: "  + e.getMessage());
            return;
        } catch (NullObjectException e) {
            fail("User session invalid: "  + e.getMessage());
            return;
        }
        catch (Exception e){
            fail(e.getMessage());
            return;
        }

    }

    /**
     * UT failed login (Invalid username)
     * */
    @Test
    public void testLoginWithUnExistingUsername() {
        String error ="";

        AppUser user = new AppUser();
        try {
            user = userRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        }
        catch(Exception e){
            //Not expected
        }
        assertEquals(1, userRepository.getNumberUsers());

        try{
            authentication.login("none","none");
        }
        //Expected
        catch(NullObjectException e){
            error += e.getMessage();
        }
        catch(Exception e){
            fail(e.getMessage());
            return;
        }

        assertEquals("User does not exist",error);
    }

    /**
     * UT failed login (Invalid password)
     * */
    @Test
    public void testLoginWithWrongPassword() throws InvalidInputException{
        String error ="";
        AppUser user = new AppUser();
        try {
            user = userRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        }
        catch(Exception e){
            //Not expected
        }
        assertEquals(1, userRepository.getNumberUsers());

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

    /**
     * UT logout (successful/fail)
     * */
    @Test
    public void testLogout(){

        AppUser user = new AppUser();
        try {
            user = userRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        }
        catch(Exception e){
            //Not expected
        }
        assertEquals(1, userRepository.getNumberUsers());

        try {
            // First login to get the session
            authentication.login(user.getUsername(), "HelloWorld123");

            // Then logout to invalidate the session
            authentication.logout(user.getUsername());

            // Usage of the invalidated session should fail
            try {
                authentication.getUserBySession(user.getUsername());
                fail("Invalidated session, no exception thrown");
            } catch (NullObjectException e) {
                // Expected
            }
        } catch (AuthenticationException e) {
            fail(e.getMessage());
            return;
        }
        catch (NullObjectException e) {
            fail(e.getMessage());
            return;
        }
        catch (Exception e){
            fail(e.getMessage());
            return;
        }
    }

}
