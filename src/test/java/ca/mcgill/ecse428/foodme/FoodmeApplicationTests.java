package ca.mcgill.ecse428.foodme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.mcgill.ecse428.foodme.controller.AppUserController;
import ca.mcgill.ecse428.foodme.controller.PreferenceController;
import ca.mcgill.ecse428.foodme.controller.RestaurantController;
import ca.mcgill.ecse428.foodme.controller.SearchController;
import ca.mcgill.ecse428.foodme.exception.AuthenticationException;
import ca.mcgill.ecse428.foodme.exception.InvalidInputException;
import ca.mcgill.ecse428.foodme.model.AppUser;
import ca.mcgill.ecse428.foodme.model.Preference;
import ca.mcgill.ecse428.foodme.model.Restaurant;
import ca.mcgill.ecse428.foodme.repository.AppUserRepository;
import ca.mcgill.ecse428.foodme.repository.PreferenceRepository;
import ca.mcgill.ecse428.foodme.repository.RestaurantRepository;
import ca.mcgill.ecse428.foodme.security.Password;


@RunWith(SpringRunner.class)
@SpringBootTest
public class FoodmeApplicationTests {

	private static final String USERNAME = "test";
	private static final String FIRSTNAME = "John";
	private static final String LASTNAME="Doe";
	private static String EMAIL="johnDoe@hotmail.ca";
	private String PASSWORD = "HelloWorld123";

    private MockMvc mockMvc;

    AppUserRepository appUserRepository = Mockito.mock(AppUserRepository.class, Mockito.RETURNS_DEEP_STUBS);
    PreferenceRepository preferenceRepository = Mockito.mock(PreferenceRepository.class, Mockito.RETURNS_DEEP_STUBS);
    RestaurantRepository restaurantRepository = Mockito.mock(RestaurantRepository.class, Mockito.RETURNS_DEEP_STUBS);

    @InjectMocks
	AppUserController appUserController;
    @InjectMocks
    PreferenceController preferenceController;
	@InjectMocks
    RestaurantController restaurantController;
	@InjectMocks
    SearchController searchController;


	/**
	 * Initializing the controller before starting all the tests
	 */
	@Before
	public void setUp()
	{
		appUserController = new AppUserController();
        preferenceController = new PreferenceController();
        restaurantController = new RestaurantController();
		searchController = new SearchController();
		MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();

    }

	@Test
	public void contextLoads() {
	}

	/**
	 * Initial test to make sure all is working. Verifies if the home page of the web site displays "Hello, World!"
	 */
	@Test
	public void testGreeting() {
		assertEquals("AppUser connected!", appUserController.greeting());
        assertEquals("Preference connected!", preferenceController.greeting());
        assertEquals("Restaurant connected!", restaurantController.greeting());
        assertEquals("Search connected!", searchController.greeting());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////                                                                   /////////////////
    /////////////////                     APP USER CONTROLLER                           /////////////////
    /////////////////                                                                   /////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testCreateAccount()
	{
		AppUser u = new AppUser();
		u.setUsername(USERNAME);
		u.setFirstName(FIRSTNAME);
		u.setLastName(LASTNAME);
		u.setEmail(EMAIL);
        String passwordHash="";
        try {
            passwordHash = Password.getSaltedHash(PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        u.setPassword(passwordHash);

        try {
            when(appUserRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD)).thenReturn(u);
            when(appUserRepository.getAppUser(USERNAME)).thenReturn(null);
            assertEquals(appUserRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD), u);
            Mockito.verify(appUserRepository).createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        }catch(Exception e){
            e.printStackTrace();
        }
	}

    @Test
    public void testDeleteUser() {
	    try{
            AppUser appUser = appUserRepository.createAccount(USERNAME,FIRSTNAME,LASTNAME,EMAIL,PASSWORD);
            when(appUserRepository.deleteUser(USERNAME)).thenReturn(appUser);
            assertEquals(appUserRepository.deleteUser(USERNAME),appUser);
            Mockito.verify(appUserRepository).deleteUser(USERNAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testChangePasswordSuccess() {
	    //If no exception caught, change pasword is successful
        String newPass = "Helloworld1234";
        String oldPass = PASSWORD;
        try {
            AppUser user = appUserRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
            when(appUserRepository.changePassword(user.getUsername(), oldPass, newPass)).thenReturn(user);
            assertEquals(appUserRepository.changePassword(user.getUsername(), oldPass, newPass),user);
            Mockito.verify(appUserRepository).changePassword(user.getUsername(), oldPass, newPass);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testChangePasswordFail() {
        String error ="";
        AppUser user = new AppUser();
        String oldPass = PASSWORD;
        String newPass = "HelloWorld1234";
        String wrongOldPass = "hahahaha"; //old pass is HelloWorld123
        String wrongNewPass = "Hello";
        try {
            user = appUserRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
            when(appUserRepository.changePassword(user.getUsername(), wrongOldPass, newPass)).thenThrow(new AuthenticationException("Invalid old password"));
            assertEquals(appUserRepository.changePassword(user.getUsername(), wrongOldPass, newPass),new AuthenticationException("Invalid old password"));
            Mockito.verify(appUserRepository).changePassword(user.getUsername(), wrongOldPass, newPass);
        }catch (AuthenticationException e) {
            //Expected
            error += e.getMessage();
        }catch(Exception e){
            //Do nothing
        }
        assertEquals(error, "Invalid old password");
        error = "";
        try {
            when(appUserRepository.changePassword(user.getUsername(), oldPass, wrongNewPass)).thenThrow(new InvalidInputException("Your password should be longer than 6 characters"));
            assertEquals(appUserRepository.changePassword(user.getUsername(), oldPass, wrongNewPass),new InvalidInputException("Your password should be longer than 6 characters"));
            Mockito.verify(appUserRepository).changePassword(user.getUsername(), oldPass, wrongNewPass);
        }catch (InvalidInputException e) {
            //Expected
            error += e.getMessage();
        }catch(Exception e){
            //Do nothing
        }
        assertEquals(error, "Your password should be longer than 6 characters");

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
    public void testDefaultPreference() {
        String location = "Montreal";
        String cuisine = "Italian";
        String priceRange = "$$$";
        String sortBy = "rating";
        try {
            AppUser appUser = appUserRepository.createAccount(USERNAME,FIRSTNAME,LASTNAME,EMAIL,PASSWORD);
            Preference newPreference = preferenceRepository.createPreference(USERNAME, priceRange, location, cuisine, sortBy);

            int pID = newPreference.getPID();

            when(appUserRepository.setDefaultPreference(pID,USERNAME)).thenReturn(pID);
            assertEquals(pID, appUserRepository.setDefaultPreference(pID,USERNAME));
            Mockito.verify(appUserRepository).setDefaultPreference(pID,USERNAME);

            Preference dfPreference = preferenceRepository.getPreference(pID);
            List<Preference> list = new ArrayList<>();
            list.add(dfPreference);

            when(appUserRepository.getDefaultPreference(USERNAME)).thenReturn(list);
            assertEquals(appUserRepository.getDefaultPreference(USERNAME),list);
            Mockito.verify(appUserRepository).getDefaultPreference(USERNAME);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////                                                                   /////////////////
    /////////////////                   PREFERENCE CONTROLLER                           /////////////////
    /////////////////                                                                   /////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testAddPreference() {
        String location = "Montreal";
        String cuisine = "Italian";
        String priceRange = "$$$";
        String sortBy = "sortBy";

        Preference newPreference = new Preference();
        newPreference.setPrice(priceRange);
        newPreference.setCuisine(cuisine);
        newPreference.setLocation(location);
        newPreference.setPID(1);
        newPreference.setSortBy(sortBy);

        try {
            AppUser appUser = appUserRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
            newPreference.setUser(appUser);
            when(preferenceRepository.createPreference(USERNAME, priceRange, location, cuisine, sortBy)).thenReturn(newPreference);
            assertEquals(preferenceRepository.createPreference(USERNAME, priceRange, location, cuisine, sortBy), newPreference);
            Mockito.verify(preferenceRepository).createPreference(USERNAME, priceRange, location, cuisine, sortBy);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testEditPreference() {
        String location = "Montreal";
        String cuisine = "Italian";
        String priceRange = "$$$";
        String sortBy = "sortBy";
        try {
            AppUser appUser = appUserRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
            Preference newPreference = preferenceRepository.createPreference(USERNAME, priceRange, location, cuisine, sortBy);
//        when(repository.createPreference(appUser, priceRange, location, cuisine, sortBy)).thenReturn(newPreference);
//        assertEquals(repository.createPreference(appUser, priceRange, location, cuisine, sortBy), newPreference);
//        Mockito.verify(repository).createPreference(appUser, priceRange, location, cuisine, sortBy);

            int pID = newPreference.getPID();

            Preference editPreference = preferenceRepository.getPreference(pID);
            location = "Montreal";
            cuisine = "Mexican";
            priceRange = "$";
            sortBy = "rating";
            editPreference.setPrice(priceRange);
            editPreference.setCuisine(cuisine);
            editPreference.setLocation(location);
            editPreference.setSortBy(sortBy);

            when(preferenceRepository.editPreference(USERNAME,pID, priceRange, location, cuisine, sortBy)).thenReturn(editPreference);
            assertEquals(preferenceRepository.editPreference(USERNAME, pID, priceRange, location, cuisine, sortBy), editPreference);
            Mockito.verify(preferenceRepository).editPreference(USERNAME, pID, priceRange, location, cuisine, sortBy);

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Test
    public void testDeletePreference() {
	    try {
            AppUser appUser = appUserRepository.createAccount(USERNAME,FIRSTNAME,LASTNAME,EMAIL,PASSWORD);

            Preference newPreference = preferenceRepository.createPreference(USERNAME, "$$$", "Montreal", "Italian", "rating");
            when(preferenceRepository.createPreference(USERNAME, "$$$", "Montreal", "Italian", "rating")).thenReturn(newPreference);
            int pID = newPreference.getPID(); // Get PID of this new preference
            when(preferenceRepository.deletePreference(USERNAME,pID)).thenReturn(newPreference);
            assertEquals(preferenceRepository.deletePreference(USERNAME, pID), newPreference);
            Mockito.verify(preferenceRepository).deletePreference(USERNAME, pID);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////                                                                   /////////////////
    /////////////////                      RESTAURANT CONTROLLER                        /////////////////
    /////////////////                                                                   /////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Test to create a restaurant
     */
    @Test
    public void testCreateRestaurantSuccess(){
        String restaurant_id = "RIIOjIdlzRyESw1BkmQHtw";
        String restaurant_name = "Tacos Et Tortas";

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantID(restaurant_id);
        restaurant.setRestaurantName(restaurant_name);
        try{
            when(restaurantRepository.createRestaurant(restaurant_id,restaurant_name,0,0)).thenReturn(restaurant);
            assertEquals(restaurantRepository.createRestaurant(restaurant_id,restaurant_name,0,0),restaurant);
            Mockito.verify(restaurantRepository).createRestaurant(restaurant_id,restaurant_name,0,0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Test to create a restaurant
     */
    @Test
    public void testCreateRestaurantFail(){
        String error ="";
        String restaurant_id = "RIIOjIdlzRyESw1BkmQHtw";
        String restaurant_name = "Tacos Et Tortas";

        Restaurant restaurant1 = new Restaurant();
        restaurant1.setRestaurantID(restaurant_id);
        restaurant1.setRestaurantName(restaurant_name);

        try{
            when(restaurantRepository.createRestaurant(restaurant_id,restaurant_name,0,0)).thenReturn(restaurant1);
            assertEquals(restaurantRepository.createRestaurant(restaurant_id,restaurant_name,0,0),restaurant1);
            Mockito.verify(restaurantRepository).createRestaurant(restaurant_id,restaurant_name,0,0);

            when(restaurantRepository.createRestaurant(restaurant_id,restaurant_name,0,0)).thenThrow(new InvalidInputException("Restaurant already exists"));
            assertEquals(restaurantRepository.createRestaurant(restaurant_id,restaurant_name,0,0),new InvalidInputException("Restaurant already exists"));
            Mockito.verify(restaurantRepository).createRestaurant(restaurant_id,restaurant_name,0,0);

        }catch(InvalidInputException e){
            error += e.getMessage();
        }
        assertEquals("Restaurant already exists",error);
        error = "";
        try{
            when(restaurantRepository.createRestaurant("","",0,0)).thenThrow(new InvalidInputException("restaurantID and restaurantName must be at least 1 character"));
            assertEquals(restaurantRepository.createRestaurant("","",0,0),new InvalidInputException("restaurantID and restaurantName must be at least 1 character"));
            Mockito.verify(restaurantRepository).createRestaurant("","",0,0);

        }catch (InvalidInputException e){
            error+=e.getMessage();
        }
        assertEquals("restaurantID and restaurantName must be at least 1 character",error);
    }


    /**
     * Test UT for adding a restaurant to the liked list
     * @throws InvalidInputException
     */
    @Test
    public void testAddLiked () {
        String restaurant_id = "RIIOjIdlzRyESw1BkmQHtw";
        String restaurant_name = "Tacos Et Tortas";

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantID(restaurant_id);
        restaurant.setRestaurantName(restaurant_name);

        try {
            AppUser user =	appUserRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
            when(restaurantRepository.createRestaurant(restaurant_id,restaurant_name,0,0)).thenReturn(restaurant);
            when(restaurantRepository.addLiked(user.getUsername(),restaurant_id,restaurant_name)).thenReturn(restaurant);
            assertEquals(restaurantRepository.addLiked(user.getUsername(),restaurant_id,restaurant_name),restaurant);
            Mockito.verify(restaurantRepository).addLiked(user.getUsername(),restaurant_id,restaurant_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test that if we like a liked restaurants, that it cannot be added twice since already liked
     */
    @Test
    public void testAddLikedAlreadyLiked(){
        String restaurant_id = "L8MXAFY14EiC_mzFCgmR_g";
        String restaurant_name = "Tacos Et Tortas";

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantID(restaurant_id);
        restaurant.setRestaurantName(restaurant_name);
        
        try {
        	AppUser user = appUserRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
			when(restaurantRepository.createRestaurant(restaurant_id,restaurant_name,0,0)).thenReturn(restaurant);
			when(restaurantRepository.addLiked(user.getUsername(),restaurant_id,restaurant_name)).thenReturn(restaurant);
			assertEquals(restaurantRepository.addLiked(user.getUsername(),restaurant_id,restaurant_name),restaurant);
			when(restaurantRepository.addLiked(user.getUsername(),restaurant_id,restaurant_name)).thenReturn(restaurant);
			assertEquals(restaurantRepository.addLiked(user.getUsername(),restaurant_id,restaurant_name),restaurant);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Test that if we dislike a disliked restaurants, that it cannot be added twice since already disliked
     */
    @Test
    public void testAddDislikedAlreadyDisliked() {
        String restaurant_id = "L8MXAFY14EiC_mzFCgmR_g";
        String restaurant_name = "Tacos Et Tortas";
        AppUser user ;
        
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantID(restaurant_id);
        restaurant.setRestaurantName(restaurant_name);
        try {
			user = appUserRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
			restaurantRepository.createRestaurant(restaurant_id,restaurant_name,0,0);
			restaurantRepository.addDisliked(user.getUsername(),restaurant_id,restaurant_name);
			assertEquals(restaurantRepository.addDisliked(user.getUsername(),restaurant_id,restaurant_name),restaurant);
			restaurantRepository.addDisliked(user.getUsername(),restaurant_id,restaurant_name);
			assertEquals(restaurantRepository.addDisliked(user.getUsername(),restaurant_id,restaurant_name),restaurant);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Test UT to add a disliked restaurant to the list of liked restaurant
     */
    @Test
    public void testAddDislikedToLiked(){
        String restaurant_id = "L8MXAFY14EiC_mzFCgmR_g";
        String restaurant_name = "Tacos Et Tortas";

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantID(restaurant_id);
        restaurant.setRestaurantName(restaurant_name);
        
        //add disliked restaurant
        try {
            AppUser user =	appUserRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
            when(restaurantRepository.createRestaurant(restaurant_id,restaurant_name,0,0)).thenReturn(restaurant);
            when(restaurantRepository.addDisliked(user.getUsername(),restaurant_id,restaurant_name)).thenReturn(restaurant);
            assertEquals(restaurantRepository.addDisliked(user.getUsername(),restaurant_id,restaurant_name),restaurant);
            Mockito.verify(restaurantRepository).addDisliked(user.getUsername(),restaurant_id,restaurant_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //try to add that disliked restaurant to the liked list 
        try {
            AppUser user =	appUserRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
            when(restaurantRepository.createRestaurant(restaurant_id,restaurant_name,0,0)).thenReturn(restaurant);
            assertFalse(restaurantRepository.listAllLiked(USERNAME).contains(restaurant_id));            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Test UT for adding a restaurant to the dislied list
     * @throws InvalidInputException
     */
    @Test
    public void testAddDisliked () throws InvalidInputException {
        String restaurant_id = "/gR9DTbKCvezQlqvD7_FzPw";
        String restaurant_name = "north-india-restaurant";

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantID(restaurant_id);
        restaurant.setRestaurantName(restaurant_name);

        try {
            AppUser user =	appUserRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
            when(restaurantRepository.createRestaurant(restaurant_id,restaurant_name,0,0)).thenReturn(restaurant);
            when(restaurantRepository.addDisliked(user.getUsername(),restaurant_id,restaurant_name)).thenReturn(restaurant);
            assertEquals(restaurantRepository.addDisliked(user.getUsername(),restaurant_id,restaurant_name),restaurant);
            Mockito.verify(restaurantRepository).addDisliked(user.getUsername(),restaurant_id,restaurant_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test UT for listing all the restaurants liked
     * @throws InvalidInputException
     */
    @Ignore
    //@Test
    public void testListAllLiked () throws InvalidInputException {
        String restaurant_id = "RIIOjIdlzRyESw1BkmQHtw";
        String restaurant_name = "Tacos Et Tortas";
        try {
            AppUser user =	appUserRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
            List<Restaurant> liked = restaurantRepository.listAllLiked(USERNAME);
            assertTrue(liked.isEmpty());
            restaurantRepository.addLiked(USERNAME, restaurant_id, restaurant_name);
            restaurantRepository.listAllLiked(USERNAME);
            assertEquals(1, liked.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Test UT for adding a restaurant to the visited list
     * @throws InvalidInputException
     */
    @Test
    public void testAddVisited () {
        String restaurant_id = "RIIOjIdlzRyESw1BkmQHtw";
        String restaurant_name = "Tacos Et Tortas";

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantID(restaurant_id);
        restaurant.setRestaurantName(restaurant_name);

        try {
            AppUser user =	appUserRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
            when(restaurantRepository.createRestaurant(restaurant_id,restaurant_name,0,0)).thenReturn(restaurant);
            when(restaurantRepository.addVisited(user.getUsername(),restaurant_id,restaurant_name)).thenReturn(restaurant);
            assertEquals(restaurantRepository.addVisited(user.getUsername(),restaurant_id,restaurant_name),restaurant);
            Mockito.verify(restaurantRepository).addVisited(user.getUsername(),restaurant_id,restaurant_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testClearVisited() {}

    public void testAllVisited(){}


//    @Test
//    public void testRestaurantList() throws InvalidInputException { //getAllRestaurants(string Location)
//        ResponseEntity<String> allRestaurant= restaurantRepository.getAllRestaurants("montreal");
//        //JSONParser parser = new JSONParser();
//        //JSONObject json = (JSONObject) parser.parse();
//        assertTrue(!Objects.isNull(allRestaurant));
//    }
//
//    @Test
//    public void testRestaurantInfo() { //getRestaurant(String id)
////
//        Object restaurant=restaurantRepository.getRestaurant("WavvLdfdP6g8aZTtbBQHTw");
////        assertTrue(restaurant.name.compareToIgnoreCase("Gary Danko"));
//        assertTrue(!Objects.isNull(restaurant));
//    }

    @Test
    public void testRemoveLike() {

	    //       AppUser user;
//	    user = repository.createAccount("Test", "Test", "Test", "Test@Test.com", "69");
//  TODO
//    	Create restaurant
//      add a like for the restaurant for user
        // remove like
        //assert if removed
    }

    @Test
    public void testRemoveDislike() {
        //       AppUser user;
//	    user = repository.createAccount("Test", "Test", "Test", "Test@Test.com", "69");
//
//    	TODO
//    	Create restaurant
//      add a dislike for the restaurant for user
        // remove dislike
        //assert if removed;
    }

}

