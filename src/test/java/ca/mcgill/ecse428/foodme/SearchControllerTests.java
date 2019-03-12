package ca.mcgill.ecse428.foodme;

import ca.mcgill.ecse428.foodme.controller.AppUserController;
import ca.mcgill.ecse428.foodme.controller.PreferenceController;
import ca.mcgill.ecse428.foodme.controller.RestaurantController;
import ca.mcgill.ecse428.foodme.controller.SearchController;
import ca.mcgill.ecse428.foodme.repository.AppUserRepository;
import ca.mcgill.ecse428.foodme.repository.PreferenceRepository;
import ca.mcgill.ecse428.foodme.repository.RestaurantRepository;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    public void testSearchSortByDistance() throws Exception {

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

    // ============== RESTAURANT RECOMMENDATION ============== //

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

    // ===================== PRICE RANGE ===================== //

    @Test
    public void testSearchByPriceLocation() throws Exception {
        String r = searchController.searchByPriceRange("Montreal", "1").getBody();
        String response = this.mockMvc.perform(get("/search/price/?location=Montreal&price=1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(r, response);
    }

    @Test
    public void testSearchByPriceLongLat() throws Exception {
        String r = searchController.searchByPriceRange("-73.623419", "45.474999", "1").getBody();
        String response = this.mockMvc.perform(get("/search/price/longitude/latitude/?longitude=-73.623419&latitude=45.474999&price=1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(r, response);
    }

    @Test //(expected = Exception.class)
    public void testSearchByPriceErrorLocation() {
        try {
            searchController.searchByPriceRange("", "1");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    @Test //(expected = Exception.class)
    public void testSearchByPriceErrorPrice() {
        try {
            searchController.searchByPriceRange("Montreal", "");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    @Test //(expected = Exception.class)
    public void testSearchByPriceErrorLong() {
        try {
            searchController.searchByPriceRange("", "45.474999", "1");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    @Test //(expected = Exception.class)
    public void testSearchByPriceErrorLat() {
        try {
            searchController.searchByPriceRange("-73.623419", "", "1");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    @Test //(expected = Exception.class)
    public void testSearchByPriceErrorLongLatPrice() {
        try {
            searchController.searchByPriceRange("-73.623419", "45.474999", "");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
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

    // ===================== CUISINE ===================== //

    @Test
    public void testSearchByCuisine() throws Exception {
        String r = searchController.searchByCuisine("Montreal", "afghan").getBody();
        String response = this.mockMvc.perform(get("/search/cuisine/?location=Montreal&cuisine=afghan"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(r, response);
    }

    @Test
    public void testSearchByCuisineLongLat() throws Exception {
        String r = searchController.searchByCuisine("-73.623419", "45.474999", "afghan").getBody();
        String response = this.mockMvc.perform(get("/search/cuisine/longitude/latitude/?longitude=-73.623419&latitude=45.474999&cuisine=afghan"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(r, response);
    }

    @Test //(expected = Exception.class)
    public void testSearchByCuisineErrorLocation() {
        try {
            searchController.searchByCuisine("", "1");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    @Test //(expected = Exception.class)
    public void testSearchByCuisineErrorPrice() {
        try {
            searchController.searchByCuisine("Montreal", "");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    @Test //(expected = Exception.class)
    public void testSearchByCuisineErrorLong() {
        try {
            searchController.searchByCuisine("", "45.474999", "1");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    @Test //(expected = Exception.class)
    public void testSearchByCuisineErrorLat() {
        try {
            searchController.searchByCuisine("-73.623419", "", "1");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
    }

    @Test //(expected = Exception.class)
    public void testSearchByCuisineErrorLongLatPrice() {
        try {
            searchController.searchByCuisine("-73.623419", "45.474999", "");
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
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
}

