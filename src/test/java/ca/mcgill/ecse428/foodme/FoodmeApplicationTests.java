package ca.mcgill.ecse428.foodme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;


import org.junit.Before;
import org.junit.Ignore;
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
    public void testAddPreference() throws InvalidInputException{
        AppUser appUser = repository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
        String distanceRange = "fivehundred";
        String cuisine = "Italian";
        String priceRange = "$$$";
        String rating = "four";

        Preference newPreference = new Preference();
        when(repository.createPreference(appUser, priceRange, distanceRange, cuisine, rating)).thenReturn(newPreference);
        assertEquals(repository.createPreference(appUser, priceRange, distanceRange, cuisine, rating), newPreference);
        Mockito.verify(repository).createPreference(appUser, priceRange, distanceRange, cuisine, rating);
    }

    @Test
    public void testEditPreference() {
        String username = "Tester123";
        AppUser appUser = repository.testCreateUser(username, "Test", "User", "student@mcgill.ca", "password");
        String distanceRange = "fivehundred";
        String cuisine = "Italian";
        String priceRange = "$$$";
        String rating = "four";

        Preference newPreference = new Preference();
        when(repository.createPreference(appUser, priceRange, distanceRange, cuisine, rating)).thenReturn(newPreference);
        assertEquals(repository.createPreference(appUser, priceRange, distanceRange, cuisine, rating), newPreference);
        Mockito.verify(repository).createPreference(appUser, priceRange, distanceRange, cuisine, rating);

        int pID = newPreference.getPID();
        Preference editPreference = repository.getPreference(pID);

        distanceRange = "fivehundred";
        cuisine = "Mexican";
        priceRange = "$";
        rating = "four";

        when(repository.editPreference(newPreference, priceRange, distanceRange, cuisine, rating)).thenReturn(editPreference);
        assertEquals(repository.editPreference(newPreference, priceRange, distanceRange, cuisine, rating), editPreference);
        Mockito.verify(repository).editPreference(newPreference, priceRange, distanceRange, cuisine, rating);
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

    @Test
    public void testGenerateRandomPassword() {
    	int lenOfPassword = 16;
    	
    	for(int i=0; i<100; i++) {
    		String p1 = Password.generateRandomPassword(lenOfPassword);
    		String p2 = Password.generateRandomPassword(lenOfPassword);
    		
    		// length should be equal
    		assertEquals(lenOfPassword, p1.length());
    		assertEquals(lenOfPassword, p2.length());
    		
    		// generated passwords should not equal, unless in extreme case
    		assertNotEquals(p1, p2);	
    	}
    }
    
    @Test
    public void testSearchSortByDistance() {
    	String response = null; // need to be replaced with the http response
    	boolean failed = false;
		Pattern p = Pattern.compile("distance\": (\\d+(\\.\\d+)?)");
		Matcher m = p.matcher(response);
		
		double a = (double) 0.0;
		// loop through all the distances, break if there is a failure  
		while (!failed && m.find()){
			double b = Double.parseDouble(m.group(1));
			if (a > b) {
				failed = true;
			}
			a = b;
		}
		assertEquals(failed, false);
		
    }
    
    
    public Restaurant helperCreateRestaurant(String restaurantID, int id) {
    	Restaurant restaurant = new Restaurant();
    	restaurant.setRestaurantName(restaurantID);
    	restaurant.setRestaurantID(id);
    	return restaurant;
    }
    
    //TODO currently merged in one with testListAll()
//    /**
//     * Test UT for adding a restaurant to the liked list
//     * @throws InvalidInputException
//     */
//    @Test
//	public void testAddLike () throws InvalidInputException {
//		AppUser user;
//	    user = repository.createAccount("Ali", "Baba", "baba", "baba@gmail.com", "22");
//
//    	String id = "E8RJkjfdcwgtyoPMjQ_Olg";
//    	helperCreateRestaurant("nameRestaurant", 11223);
//
//    	assertEquals(0, user.getLikesAnsDislikes().size());
//	    repository.addLiked(USERNAME, id);
//	    assertEquals(1, user.getLikesAnsDislikes().size());
//	}
    
    /**
     * Test UT for listing all the restaurants liked
     * @throws InvalidInputException
     */
	@Ignore
    //@Test
	public void testListAll () throws InvalidInputException {
		AppUser user;
		String id = "E8RJkjfdcwgtyoPMjQ_Olg";
		user = repository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
	    List<Restaurant> liked = repository.listAllLiked(USERNAME);
		assertTrue(liked.isEmpty());
		repository.addLiked(USERNAME, id);
		
		repository.listAllLiked(USERNAME);
		assertEquals(1, liked.size());
	}
}

