package ca.mcgill.ecse428.foodme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.mcgill.ecse428.foodme.controller.AppUserController;
import ca.mcgill.ecse428.foodme.controller.PreferenceController;
import ca.mcgill.ecse428.foodme.controller.RestaurantController;
import ca.mcgill.ecse428.foodme.controller.SearchController;
import ca.mcgill.ecse428.foodme.repository.AppUserRepository;
import ca.mcgill.ecse428.foodme.repository.PreferenceRepository;
import ca.mcgill.ecse428.foodme.repository.RestaurantRepository;

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
    
    @Test
    public void testSearchSortByDistanceFailure() throws Exception {
    	this.mockMvc.perform(get("/search/montreal/distance/"))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();
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
    
    @Test
    public void testRandomRestaurantRecommendationFailure() throws Exception {

        try {
            searchController.searchSortByDistance("abc", "123", 1);
        } catch (Exception e) {
            assertEquals("Something went wrong! Please make sure you've put in the right information!", e.getMessage());
        }
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

    // ===================== REASTAURANT INFO ===================== //
    
    @Test
    public void tesListRestaurantInfo() throws Exception {
       	
    	String id = "WavvLdfdP6g8aZTtbBQHTw";
    	String request_url = "/search/businesses/?id=" + id;
        String response1 = searchController.lookUpRestaurantByID(id).getBody();
        
        String response2 = this.mockMvc.perform(get(request_url))
                .andDo(print()).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(response1, response2);
    }
    
    @Test
    public void tesListRestaurantInfoFailure() throws Exception {
        String response1 = this.mockMvc.perform(get("/search/businesses/"))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        String response2 = this.mockMvc.perform(get("/search/businesses/"))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();

        assertEquals(response1, response2);
    	
    }
    
    
    //copying to make the test
    //TODO Marine
    //TODO tdd will move to controller method either Search or Restaurant
    //maybe subdivide into methods too...
    //@Test
    @Ignore
    public void testClosingInOneHour() throws Exception {
       	
    	String id = "WavvLdfdP6g8aZTtbBQHTw";
    	String request_url = "/search/businesses/?id=" + id;
        String response1 = searchController.lookUpRestaurantByID(id).getBody();
        
        
        String endTime = "";
//        String response2 = this.mockMvc.perform(get(request_url))
//                .andDo(print()).andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
        
        //getCurrentDate //TODO check for day {Monday0, Tuesday1,...}
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    	LocalDateTime now = LocalDateTime.now();
    	String timeNow = dtf.format(now);
    	LocalDate date = LocalDate.now();
    	LocalTime time = LocalTime.now();
    	
    	DayOfWeek dayOfWeek = date.getDayOfWeek();
    	int dayOfWeekInInteger = 0;
    	
    	//convert to the day
    	switch(dayOfWeek) {
    	case MONDAY: dayOfWeekInInteger = 0;
    	break;
    	case TUESDAY: dayOfWeekInInteger = 1;
    	break;
    	case WEDNESDAY: dayOfWeekInInteger = 2;
    	break;
    	case THURSDAY: dayOfWeekInInteger = 3;
    	break;
    	case FRIDAY: dayOfWeekInInteger = 4;
    	break;
    	case SATURDAY: dayOfWeekInInteger = 5;
    	break;
    	case SUNDAY: dayOfWeekInInteger = 6;
    	break;
    	default: dayOfWeekInInteger = 0;
    	break;
    	}
    	System.out.println("date and time:" + date + " "+time +"day in int: "+dayOfWeekInInteger);
    	

        MvcResult resp = this.mockMvc.perform(get(request_url))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        System.out.println("------------json get hours--------");
        String contentAsString = resp.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        JSONArray hours = json.getJSONArray("hours");
        int length = hours.length();
        

        for(int i =0 ; i<length; i++){
        	JSONObject allOpeningHours = hours.getJSONObject(i);
        	JSONArray open = allOpeningHours.getJSONArray("open");
        	int days = open.length();
        	System.out.println("day: "+days);
        	  for(int j=0; j<days; j++) {
        	    JSONObject json3 = open.getJSONObject(j);
                System.out.println("------------json in loop by day--------");
        	    System.out.println(json3.toString());
        	    int day = json3.getInt("day");
        	    System.out.println(day);
        	    
        	    //check if the same DAY in int
        	    if(dayOfWeekInInteger==day) {
        	    	endTime = json3.getString("end");
        	    	//TODO calculate 1 hour before using the LocalTime time 23:32:09
        	    }
        	    //open_days.add(json3.getString("name").toString());
        	  }
        	
        }
        
        
 
        
//        MvcResult res = mockMvc.perform(get(request_url)
//        		.param(id))
//        		.andDo(print())
//        		.andExpect(status().isOk())
//        		.andReturn();
        
        //System.out.println("sysout: "+ res.getResponse().getContentAsString());

//        Response resp = (Response)
//        		given().
//        		header().
//        		param("id", id).
//        		log().all().
//        		when().
//        		get(request_url).
//        		then().
//        		contentType(ContentType.JSON).extract().response();
        
        
       // assertEquals(response1, response2);
    }
    
 
}




