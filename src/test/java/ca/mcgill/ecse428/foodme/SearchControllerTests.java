package ca.mcgill.ecse428.foodme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.mcgill.ecse428.foodme.controller.AppUserController;
import ca.mcgill.ecse428.foodme.controller.PreferenceController;
import ca.mcgill.ecse428.foodme.controller.RestaurantController;
import ca.mcgill.ecse428.foodme.controller.SearchController;
import ca.mcgill.ecse428.foodme.repository.AppUserRepository;
import ca.mcgill.ecse428.foodme.repository.PreferenceRepository;
import ca.mcgill.ecse428.foodme.repository.RestaurantRepository;

/**
 * This class serves to test controller methods related to search (calling the Yelp API)
 * SearchController.java
 * */

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchControllerTests {

    private static final String USERNAME = "test";
    private static final String FIRSTNAME = "John";
    private static final String LASTNAME = "Doe";
    private static String EMAIL = "johnDoe@hotmail.ca";
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
    public void setUp() {
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
    /////////////////                      SEARCH CONTROLLER                            /////////////////
    /////////////////                                                                   /////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    
    // ===================== DISTANCE ===================== //

    /**
     * CT Search sort by distance and location - success
     * */
    @Test
    public void testSearchSortByDistanceLocation() throws Exception {
        String r = searchController.searchSortByDistance("Montreal", "distance", 0).getBody();
        String response = this.mockMvc.perform(get("/search/Montreal/distance/0/"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(r, response);
    }

    /**
     * CT Search sort by distance and location (long/lat) - success
     * */
    @Test
    public void testSearchSortByDistanceLongLat() throws Exception {
        String r = searchController.searchByLongLat("-73.623419", "45.474999", "distance", 0).getBody();
        String response = this.mockMvc.perform(get("/search/distance/0/?longitude=-73.623419&latitude=45.474999"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(r, response);
    }

    /**
     * CT Search sort by distance and invalid location - fail
     * */
    @Test //(expected = Exception.class)
    public void testSearchSortByDistanceErrorLocation() {
        try {
            searchController.searchSortByDistance("", "distance", 1);
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    /**
     * CT Search sort by distance and invalid sortby - fail
     * */
    @Test //(expected = Exception.class)
    public void testSearchSortByDistanceErrorSortBy() {
        try {
            searchController.searchSortByDistance("Montreal", "", 1);
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    /**
     * CT Search sort by distance - success
     * */
    @Test
    public void testHTTPSearchSortByDistance() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get("/search/montreal/distance/0/"))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        System.out.println("\n\nResponse:");
        System.out.println(response);
        boolean failed = false;
        Pattern p = Pattern.compile("distance\": (\\d+(\\.\\d+)?)");
        Matcher m = p.matcher(response);

        double a = 0.0;
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

    /**
     * CT Search sort by distance - fail
     * */
    @Test
    public void testHTTPSearchSortByDistanceFailure() throws Exception {
    	this.mockMvc.perform(get("/search/montreal/distance/"))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();
    }

    // ============== RESTAURANT RECOMMENDATION ============== //

    /**
     * CT Search Random Restaurant Recommendation - success
     * */
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

    /**
     * CT Search Random Restaurant Recommendation - fail
     * */
    @Test
    public void testRandomRestaurantRecommendationFailure() {

        try {
            searchController.searchSortByDistance("abc", "123", 1);
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    // ===================== PRICE RANGE ===================== //

    /**
     * CT Search by price and location - success
     * */
    @Test
    public void testSearchByPriceLocation() throws Exception {
        String r = searchController.searchByPriceRange("Montreal", "1").getBody();
        String response = this.mockMvc.perform(get("/search/price/?location=Montreal&price=1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(r, response);
    }

    /**
     * CT Search by price and location (long/lat) - success
     * */
    @Test
    public void testSearchByPriceLongLat() throws Exception {
        String r = searchController.searchByPriceRange("-73.623419", "45.474999", "1").getBody();
        String response = this.mockMvc.perform(get("/search/price/longitude/latitude/?longitude=-73.623419&latitude=45.474999&price=1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(r, response);
    }

    /**
     * CT Search by price and location - fail
     * */
    @Test
    public void testSearchByPriceErrorLocation() {
        try {
            searchController.searchByPriceRange("", "1");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    /**
     * CT Search by invalid price - fail
     * */
    @Test
    public void testSearchByPriceErrorPrice() {
        try {
            searchController.searchByPriceRange("Montreal", "");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    /**
     * CT Search by price and invalid location(long) - fail
     * */
    @Test //(expected = Exception.class)
    public void testSearchByPriceErrorLong() {
        try {
            searchController.searchByPriceRange("", "45.474999", "1");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    /**
     * CT Search by price and invalid location(lat) - fail
     * */
    @Test //(expected = Exception.class)
    public void testSearchByPriceErrorLat() {
        try {
            searchController.searchByPriceRange("-73.623419", "", "1");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    /**
     * CT Search by invalid price and  location(long/lat) - fail
     * */
    @Test //(expected = Exception.class)
    public void testSearchByPriceErrorLongLatPrice() {
        try {
            searchController.searchByPriceRange("-73.623419", "45.474999", "");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    /**
     * CT Search by price http - success
     * */
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

    /**
     * CT Search by price and location(long/lat) http - success
     * */
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

    /**
     * CT Search by price http - fail
     * */
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

    /**
     * CT Search by price and location(long/lat) http - fail
     * */
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

    // ===================== CUISINE ===================== //

    /**
     * CT Search by cuisine - success
     * */
    @Test
    public void testSearchByCuisine() throws Exception {
        String r = searchController.searchByCuisine("Montreal", "afghan").getBody();
        String response = this.mockMvc.perform(get("/search/cuisine/?location=Montreal&cuisine=afghan"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(r, response);
    }

    /**
     * CT Search by cuisine and location(long/lat) - fail
     * */
    @Test
    public void testSearchByCuisineLongLat() throws Exception {
        String r = searchController.searchByCuisine("-73.623419", "45.474999", "afghan").getBody();
        String response = this.mockMvc.perform(get("/search/cuisine/longitude/latitude/?longitude=-73.623419&latitude=45.474999&cuisine=afghan"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(r, response);
    }

    /**
     * CT Search by cuisine and invalid location - fail
     * */
    @Test
    public void testSearchByCuisineErrorLocation() {
        try {
            searchController.searchByCuisine("", "1");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    /**
     * CT Search by cuisine and invalid price - fail
     * */
    @Test
    public void testSearchByCuisineErrorPrice() {
        try {
            searchController.searchByCuisine("Montreal", "");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    /**
     * CT Search by cuisine and invalid location(long) - fail
     * */
    @Test
    public void testSearchByCuisineErrorLong() {
        try {
            searchController.searchByCuisine("", "45.474999", "1");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    /**
     * CT Search by cuisine and invalid location(lat) - fail
     * */
    @Test //(expected = Exception.class)
    public void testSearchByCuisineErrorLat() {
        try {
            searchController.searchByCuisine("-73.623419", "", "1");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    /**
     * CT Search by cuisine and invalid location - fail
     * */
    @Test
    public void testSearchByCuisineErrorLongLatPrice() {
        try {
            searchController.searchByCuisine("-73.623419", "45.474999", "");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    /**
     * CT Search by cuisine http - success
     * */
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

    /**
     * CT Search by cuisine and location(long/lat) http - success
     * */
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

    /**
     * CT Search by cuisine http - fail
     * */
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

    /**
     * CT Search by cuisine and location(long/lat) http - fail
     * */
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

    // ===================== RESTAURANT INFO ===================== //

    /**
     * CT list restaurant info - success
     * */
    @Test
    public void testListRestaurantInfoSuccess() throws Exception {
       	
    	String id = "WavvLdfdP6g8aZTtbBQHTw";
    	String request_url = "/search/businesses/?id=" + id;
        String response1 = searchController.lookUpRestaurantByID(id).getBody();
        
        String response2 = this.mockMvc.perform(get(request_url))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(response1, response2);
    }

    /**
     * CT list restaurant info - fail
     * */
    @Test
    public void testListRestaurantInfoFailure() throws Exception {
        try {
            searchController.lookUpRestaurantByID("");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    /**
     * CT list restaurant info http - success
     * */
    @Test
    public void testListRestaurantInfoHTTPSuccess() throws Exception {
        String response1 = this.mockMvc.perform(get("/search/businesses/?id=WavvLdfdP6g8aZTtbBQHTw"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String response2 = this.mockMvc.perform(get("/search/businesses/?id=WavvLdfdP6g8aZTtbBQHTw"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(response1, response2);
    }

    /**
     * CT list restaurant http - fail
     * */
    @Test
    public void testListRestaurantInfoHTTPFailure() throws Exception {
        String response1 = this.mockMvc.perform(get("/search/businesses/"))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        String response2 = this.mockMvc.perform(get("/search/businesses/"))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        assertEquals(response1, response2);
    }
    
    @Test
    public void testClosingOneHour() {
            String response="";
			String expected = "{\"response\":false,\"message\":\"The restaurant is still open.\"}";
			try {
				response = this.mockMvc.perform(get("/search/get/closing/?id=5T6kFKFycym_GkhgOiysIw"))
				        .andExpect(status().isOk())
				        .andReturn().getResponse().getContentAsString();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			assertEquals(expected,response);
        }
}
