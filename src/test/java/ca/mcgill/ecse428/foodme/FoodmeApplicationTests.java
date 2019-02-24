package ca.mcgill.ecse428.foodme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import ca.mcgill.ecse428.foodme.controller.Controller;
import ca.mcgill.ecse428.foodme.model.*;
import ca.mcgill.ecse428.foodme.repository.FoodmeRepository;
import ca.mcgill.ecse428.foodme.repository.InvalidInputException;
import ca.mcgill.ecse428.foodme.security.Password;
import ca.mcgill.ecse428.foodme.service.AuthenticationException;
import ca.mcgill.ecse428.foodme.service.AuthenticationService;
import ca.mcgill.ecse428.foodme.service.InvalidSessionException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FoodmeApplicationTests 
{
	private static final String testUsername = "Tester123";
	private static final String testFirstName = "Test";
	private static final String testLastName = "User";
	private static final String testEmail = "student@mcgill.ca";
	private static final String testPassword = "password";
	
	private static final String USERNAME = "test";
	private static final String FIRSTNAME = "John";
	private static final String LASTNAME="Doe";
	private static String EMAIL="johnDoe@hotmail.ca";
	private String PASSWORD = "HelloWorld123";
	
    @Autowired
    private AuthenticationService authentication;
	
	@InjectMocks
	Controller controller;

	@Mock
	FoodmeRepository repository = Mockito.mock(FoodmeRepository.class);

	/**
	 * Initializing the controller before starting all the tests
	 */
	@Before
	public void setUp()
	{
		controller = new Controller();
		MockitoAnnotations.initMocks(this);
	}
	
	@Before
	public void setMockOutput() throws InvalidInputException {
	    try {
            AppUser user = new AppUser();
            user.setUsername(USERNAME);
            user.setLastName(LASTNAME);
            user.setFirstName(FIRSTNAME);
            user.setPassword(Password.getSaltedHash(PASSWORD));
            user.setEmail(EMAIL);

            Mockito.when(repository.getNumberUsers()).thenReturn(1);
            Mockito.when(repository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD)).thenReturn(user);
            Mockito.when(repository.getAppUser(USERNAME)).thenReturn(user);
            Mockito.when(repository.getAppUser("none")).thenReturn(null);
        }
	    catch(Exception e){
	        e.printStackTrace();
        }
	}
	
	@Test
	public void contextLoads() {
	}
	
	/**
	 * Initial test to make sure all is working. Verifies if the home page of the web site displays "Hello, World!" 
	 */
	@Test
	public void testGreeting() 
	{
		assertEquals("Hello world!", controller.greeting());	
	}
	
	@Test
	public void testTestCreateUser() 
	{
		AppUser u = new AppUser();
		u.setUsername(testUsername);
		u.setFirstName(testFirstName);
		u.setLastName(testLastName);
		u.setEmail(testEmail);
		u.setPassword(testPassword);
		u.setPreferences(new ArrayList<Preference>());                                                                                      
		u.setLikesAnsDislikes(new ArrayList<Restaurant>());

		when(repository.testCreateUser(testUsername,testFirstName,testLastName,testEmail,testPassword)).thenReturn(u);
		assertEquals(controller.testCreateUser(testUsername,testFirstName,testLastName,testEmail,testPassword),u);
		Mockito.verify(repository).testCreateUser(testUsername,testFirstName,testLastName,testEmail,testPassword);
	}
	
    @Test
    public void testDeleteUser() 
    {
    	AppUser appUser;
        if(repository.getAppUser(testUsername) == null)
        {
        	appUser = repository.testCreateUser(testUsername,testFirstName,testLastName,testEmail,testPassword);
        }
        else
        {
        	appUser = repository.getAppUser("Tester123");
        }
        String username = appUser.getUsername();
        repository.deleteUser(username);
        assertEquals(repository.getAppUser(testUsername), null);
    }
    
    @Test
    public void testAddPreference() {
        AppUser appUser;
        if(repository.getAppUser("Tester123") == null) // Create new user if doesn't exist
            appUser = repository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
        else
            appUser = repository.getAppUser("Tester123");
        String distanceRange = "fivehundred";
        String cuisine = "Italian";
        String priceRange = "$$$";
        String rating = "four";

        repository.createPreference(appUser, priceRange, distanceRange, cuisine, rating); // Create new preference
        assertEquals(appUser.getPreferences().size(), 1);
    }

    @Test
    public void testEditPreference() {
        AppUser appUser;
        String username = "Tester123";
        Preference editPreference = null;
        if(repository.getAppUser(username) == null) // Create new user if doesn't exist
            appUser = repository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
        else
            appUser = repository.getAppUser("Tester123");

        Preference newPreference = repository.createPreference(appUser, "$$$", "fivehundred", "Italian", "four"); // Create new preference
        int pID = newPreference.getPID(); // Get PID of this new preference

        editPreference = repository.getPreference(pID);

        String distanceRange = "fivehundred";
        String cuisine = "Mexican";
        String priceRange = "$";
        String rating = "four";

        editPreference = repository.editPreference(editPreference, priceRange, distanceRange, cuisine, rating);
        assertEquals(editPreference.getPrice(), PriceRange.$); // Check to see that the price range changed!
        assertEquals(editPreference.getPID(), pID); // Make sure PID didn't change
    }
    
    @Test
    public void testLoginWithValidPassword() throws InvalidInputException{

	    AppUser user;
	    try {
            user = repository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        }
	    catch(InvalidInputException e){
            throw new InvalidInputException("Invalid input format.");
        }

	    assertEquals(1, repository.getNumberUsers());

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
            user = repository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        }
        catch(InvalidInputException e){
            throw new InvalidInputException("Invalid input format.");
        }

        assertEquals(1, repository.getNumberUsers());

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
            user = repository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        }
        catch(InvalidInputException e){
            throw new InvalidInputException("Invalid input format.");
        }

        assertEquals(1, repository.getNumberUsers());


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
            user = repository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        }
        catch(InvalidInputException e){
            throw new InvalidInputException("Invalid input format.");
        }

        assertEquals(1, repository.getNumberUsers());

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

    /**
     * Test UT for adding a restaurant to the liked list
     * @throws InvalidInputException
     */
    @Test
	public void testAddLike () throws InvalidInputException {
		AppUser user;
    	String id = "E8RJkjfdcwgtyoPMjQ_Olg";
	    user = repository.createAccount("Ali", "Baba", "baba", "baba@gmail.com", "22");
	    assertEquals(0, user.getLikesAnsDislikes().size());
	    //repository.addLiked(USERNAME, id);
	    //assertEquals(1, user.getLikesAnsDislikes().size());
	}
    
    /**
     * Test UT for listing all the restaurants liked
     * @throws InvalidInputException
     */
	@Test
	public void testListAll () throws InvalidInputException {
		int count =0;
		AppUser user;
	    List<Restaurant> liked = repository.listAllLiked(USERNAME);
	    user = repository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
	    
	    for (Restaurant r: user.getLikesAnsDislikes()) {
	    	if(user.getLikesAnsDislikes().isEmpty()) {
	    		count=0;
	    	}
	    	else if(r.isLiked()) {
	    		count++;
	    	}
	    }
	    assertEquals(liked.size(), count);
	}
}

