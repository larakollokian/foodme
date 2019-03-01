package ca.mcgill.ecse428.foodme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;


import jdk.nashorn.internal.parser.JSONParser;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import ca.mcgill.ecse428.foodme.controller.Controller;
import ca.mcgill.ecse428.foodme.model.*;
import ca.mcgill.ecse428.foodme.repository.FoodmeRepository;
import ca.mcgill.ecse428.foodme.repository.InvalidInputException;
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

    FoodmeRepository repository = Mockito.mock(FoodmeRepository.class, Mockito.RETURNS_DEEP_STUBS);

	@InjectMocks
	Controller controller;

	/**
	 * Initializing the controller before starting all the tests
	 */
	@Before
	public void setUp()
	{
		controller = new Controller();
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
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
        u.setDefaultPreferenceID(Integer.MIN_VALUE);

		when(repository.createAccount(USERNAME,FIRSTNAME,LASTNAME,EMAIL,PASSWORD)).thenReturn(u);
		when(controller.getAppUser(USERNAME)).thenReturn(null);
        try {
            assertEquals(controller.createAccount(USERNAME,FIRSTNAME,LASTNAME,EMAIL,PASSWORD),u);
        } catch (InvalidInputException e) {
            e.printStackTrace();
        }
        Mockito.verify(repository).createAccount(USERNAME,FIRSTNAME,LASTNAME,EMAIL,PASSWORD);
	}

    @Test
    public void testDeleteUser() {
        AppUser appUser = repository.createAccount(USERNAME,FIRSTNAME,LASTNAME,EMAIL,PASSWORD);

        when(repository.createAccount(USERNAME,FIRSTNAME,LASTNAME,EMAIL,PASSWORD)).thenReturn(appUser);
        try {
            repository.deleteUser(USERNAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Mockito.verify(repository).deleteUser(USERNAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddPreference() {
        AppUser appUser = repository.createAccount(USERNAME,FIRSTNAME,LASTNAME,EMAIL,PASSWORD);
        String location = "Montreal";
        String cuisine = "Italian";
        String priceRange = "$$$";
        String sortBy = "sortBy";

        Preference newPreference = new Preference();
        when(repository.createPreference(appUser, priceRange, location, cuisine, sortBy)).thenReturn(newPreference);
        assertEquals(repository.createPreference(appUser, priceRange, location, cuisine, sortBy), newPreference);
        Mockito.verify(repository).createPreference(appUser, priceRange, location, cuisine, sortBy);
    }

    @Test
    public void testEditPreference() {
        AppUser appUser = repository.createAccount(USERNAME,FIRSTNAME,LASTNAME,EMAIL,PASSWORD);
        String location = "Montreal";
        String cuisine = "Italian";
        String priceRange = "$$$";
        String sortBy = "sortBy";

        Preference newPreference = new Preference();
        when(repository.createPreference(appUser, priceRange, location, cuisine, sortBy)).thenReturn(newPreference);
        assertEquals(repository.createPreference(appUser, priceRange, location, cuisine, sortBy), newPreference);
        Mockito.verify(repository).createPreference(appUser, priceRange, location, cuisine, sortBy);

        int pID = newPreference.getPID();
        Preference editPreference = null;
        try {
            editPreference = repository.getPreference(pID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        location = "Montreal";
        cuisine = "Mexican";
        priceRange = "$";
        sortBy = "rating";

        when(repository.editPreference(newPreference, priceRange, location, cuisine, sortBy)).thenReturn(editPreference);
        assertEquals(repository.editPreference(newPreference, priceRange, location, cuisine, sortBy), editPreference);
        Mockito.verify(repository).editPreference(newPreference, priceRange, location, cuisine, sortBy);
    }


    @Test
    public void testDeletePreference() {

        AppUser appUser = repository.createAccount(USERNAME,FIRSTNAME,LASTNAME,EMAIL,PASSWORD);

        Preference newPreference = repository.createPreference(appUser, "$$$", "Montreal", "Italian", "rating");
        when(repository.createPreference(appUser, "$$$", "Montreal", "Italian", "rating")).thenReturn(newPreference);
        int pID = newPreference.getPID(); // Get PID of this new preference
        repository.deletePreference(pID);
        Mockito.verify(repository).deletePreference(pID);

    }

    @Test
    public void testDefaultPreference() {
        AppUser appUser = repository.createAccount(USERNAME,FIRSTNAME,LASTNAME,EMAIL,PASSWORD);
        String location = "Montreal";
        String cuisine = "Italian";
        String priceRange = "$$$";
        String sortBy = "rating";

        Preference newPreference = repository.createPreference(appUser, priceRange, location, cuisine, sortBy);
   //     when(repository.createPreference(appUser, priceRange, location, cuisine, sortBy)).thenReturn(newPreference);
   //     assertEquals(repository.createPreference(appUser, priceRange, location, cuisine, sortBy), newPreference);
   //     Mockito.verify(repository).createPreference(appUser, priceRange, location, cuisine, sortBy);

        int pID = newPreference.getPID();
        try {
            repository.setDefaultPreference(pID,USERNAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            assertEquals(pID, repository.getDefaultPreference(USERNAME).getPID());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Preference dfPreference = null;
        try {
            dfPreference = repository.getPreference(pID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            when(repository.getDefaultPreference(USERNAME)).thenReturn(dfPreference);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Mockito.verify(repository).getDefaultPreference(USERNAME);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @Test
    public void testChangePassword() throws InvalidInputException {
    	AppUser user = repository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);

    	String newPass = "Hello";
    	try {
            repository.changePassword(user.getUsername(), PASSWORD, newPass);
        }catch (Exception e) {
            e.printStackTrace();
        }
    	try{
    	    assertTrue(Password.check(newPass,user.getPassword()));

    	}catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testRestaurantList() throws InvalidInputException { //getAllRestaurants(string Location)


        ResponseEntity<String> allRestaurant= repository.getAllRestaurants("montreal");
        //JSONParser parser = new JSONParser();
        //JSONObject json = (JSONObject) parser.parse();
        assertTrue(!Objects.isNull(allRestaurant));
    }

    @Test
    public void testRestaurantInfo() { //getRestaurant(String id)
//
        Object restaurant=repository.getRestaurant("WavvLdfdP6g8aZTtbBQHTw");
//        assertTrue(restaurant.name.compareToIgnoreCase("Gary Danko"));
        assertTrue(!Objects.isNull(restaurant));
    }

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
    public void testSearchSortByDistance() throws Exception {

    	MvcResult mvcResult = this.mockMvc.perform(get("/search/montreal/distance/0/"))
    							 .andDo(print())
    							 .andExpect(status().isOk())
    							 .andReturn();
    	String response = mvcResult.getResponse().getContentAsString();
    	System.out.println("\n\nResponse:");
    	System.out.println(response);
    	//String response = ""; // TODO: need to be replaced with the http response
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

    @Test
    public void testRandomRestaurantRecommendation() throws Exception {

		String response1 = this.mockMvc.perform(get("/search/montreal/distance/1/"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		String response2 = this.mockMvc.perform(get("/search/montreal/distance/1/"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		assertNotEquals(response1, response2);
	}

	@Test
	public void testSearchByPriceHTTPOk() throws Exception {

		String response1 = this.mockMvc.perform(get("/search/price/?location=montreal&price=1"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		String response2 = this.mockMvc.perform(get("/search/price/?location=montreal&price=1"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		assertEquals(response1, response2);
	}

	@Test
	public void testSearchByPriceLongLatHTTPOk() throws Exception {

		String response1 = this.mockMvc.perform(get("/search/price/longitude/latitude/?longitude=-73.623419&latitude=45.474999&price=1"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		String response2 = this.mockMvc.perform(get("/search/price/longitude/latitude/?longitude=-73.623419&latitude=45.474999&price=1"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		assertEquals(response1, response2);
	}

	@Test
	public void testSearchByPriceHTTPNotOk() throws Exception {

		String response1 = this.mockMvc.perform(get("/search/price/?price=1"))
				.andExpect(status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();

		String response2 = this.mockMvc.perform(get("/search/price/?price=1"))
				.andExpect(status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();

		assertEquals(response1, response2);
	}

	@Test
	public void testSearchByPriceLongLatHTTPNotOk() throws Exception {

		String response1 = this.mockMvc.perform(get("/search/price/longitude/latitude/?latitude=45.474999&price=1"))
				.andExpect(status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();

		String response2 = this.mockMvc.perform(get("/search/price/longitude/latitude/?latitude=45.474999&price=1"))
				.andExpect(status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();

		assertEquals(response1, response2);
	}

	@Test
	public void testSearchByCuisineHTTPOk() throws Exception {

		String response1 = this.mockMvc.perform(get("/search/cuisine/?location=montreal&cuisine=afghan"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		String response2 = this.mockMvc.perform(get("/search/cuisine/?location=montreal&cuisine=afghan"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		assertEquals(response1, response2);
	}

	@Test
	public void testSearchByCuisineLongLatHTTPOk() throws Exception {

		String response1 = this.mockMvc.perform(get("/search/cuisine/longitude/latitude/?longitude=-73.623419&latitude=45.474999&cuisine=afghan"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		String response2 = this.mockMvc.perform(get("/search/cuisine/longitude/latitude/?longitude=-73.623419&latitude=45.474999&cuisine=afghan"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		assertEquals(response1, response2);
	}

	@Test
	public void testSearchByCuisineHTTPNotOk() throws Exception {

		String response1 = this.mockMvc.perform(get("/search/cuisine/?price=afghan"))
				.andExpect(status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();

		String response2 = this.mockMvc.perform(get("/search/cuisine/?price=afghan"))
				.andExpect(status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();

		assertEquals(response1, response2);
	}

	@Test
	public void testSearchByCuisineLongLatHTTPNotOk() throws Exception {

		String response1 = this.mockMvc.perform(get("/search/cuisine/longitude/latitude/?latitude=45.474999&cuisine=afghan"))
				.andExpect(status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();

		String response2 = this.mockMvc.perform(get("/search/cuisine/longitude/latitude/?latitude=45.474999&cuisine=afghan"))
				.andExpect(status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();

		assertEquals(response1, response2);
	}

	public Restaurant helperCreateRestaurant(String restaurantID, String restaurantName) {
    	Restaurant restaurant = new Restaurant();
    	restaurant.setRestaurantName(restaurantName);
    	restaurant.setRestaurantID(restaurantID);
    	return restaurant;
    }

 //   TODO currently merged in one with testListAll()

//	@Test
//	public void testAddPreference() throws InvalidInputException{
//		AppUser appUser = repository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
//		String location = "Montreal";
//		String cuisine = "Italian";
//		String priceRange = "$$$";
//		String sortBy = "rating";
//
//		Preference newPreference = new Preference();
//		when(repository.createPreference(appUser, priceRange, location, cuisine, sortBy)).thenReturn(newPreference);
//		assertEquals(repository.createPreference(appUser, priceRange, location, cuisine, sortBy), newPreference);
//		Mockito.verify(repository).createPreference(appUser, priceRange, location, cuisine, sortBy);
//	}

	/**
     * Test UT for adding a restaurant to the liked list
     * @throws InvalidInputException
     */
    @Test
	public void testAddLiked () throws InvalidInputException {
		AppUser user =	repository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
        String restaurant_id = "E8RJkjfdcwgtyoPMjQ_Olg";
		String restaurant_name = "Tim Hortons";

		Restaurant restaurant = new Restaurant();
		restaurant.setRestaurantID(restaurant_id);
        restaurant.setRestaurantName(restaurant_name);

		try {
			when(repository.addLiked(user.getUsername(),restaurant_id,restaurant_name)).thenReturn(restaurant);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			assertEquals(repository.addLiked(user.getUsername(),restaurant_id,restaurant_name),restaurant);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Mockito.verify(repository).addLiked(user.getUsername(),restaurant_id,restaurant_name);
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
	public void testListAll () throws InvalidInputException {
        AppUser user =	repository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
		String restaurant_id = "E8RJkjfdcwgtyoPMjQ_Olg";
        String restaurant_name = "Tim Hortons";
        List<String> liked = repository.listAllLiked(USERNAME);
		assertTrue(liked.isEmpty());
		try {
			repository.addLiked(USERNAME, restaurant_id, restaurant_name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		repository.listAllLiked(USERNAME);
		assertEquals(1, liked.size());
	}
}

