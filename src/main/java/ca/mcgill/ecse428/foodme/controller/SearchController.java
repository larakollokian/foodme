package ca.mcgill.ecse428.foodme.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import ca.mcgill.ecse428.foodme.model.Response;

import java.util.Random;

import static org.springframework.web.client.HttpClientErrorException.*;

@RestController
@RequestMapping("/search")
public class SearchController {

    String APIKey = "F5ByVWSif5NWb6w3YYAQjRGOI9Xcg8WKqzBDkPnEl4YDneNpsaKn35YcFEqJyvyV_kUTStuTG2n9-Pi9R7-u9GIkmBQY8LjfNJSrAVEs_K5pGJLCAsWc4N3oxGRgXHYx";
    String googleApiKey = "AIzaSyAbDiuDSRG-3oyFUzlS0SOy1g5b0n49dus";

    
    /**
     * Greeting
     * @return Search connected
     */
    @RequestMapping("/")
    public String greeting() {
        return "Search connected!";
    }

    //Template
    public ResponseEntity<String> getMapping(String url) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + APIKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Response
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response;
        } catch (HttpClientErrorException e){
            throw new Exception("Something went wrong! Please make sure you've put in the right information!");
        }
    }

    /**
     * Controller method that calls the API to return a restaurant based on its id
     * @param id
     * @return Restaurant and all its information associated with it
     * @throws Exception
     */
    @GetMapping("/businesses/")
    public ResponseEntity<String> lookUpRestaurantByID (
    		@RequestParam("id") String id) throws Exception{

        // Set up url
        String url = "https://api.yelp.com/v3/businesses/" + id;
        return getMapping(url);
    }

    /**
     * Method that searches restaurants based on type of cuisine, must select from the list of cuisines available in the yelp API
     * Due to the API's limits we can only return restaurants that currently have a review
     * @param cuisine
     * @param location
     * @return
     * @throws Exception
     */
    @GetMapping("/cuisine/")
    public ResponseEntity<String> searchByCuisine (
            @RequestParam("location") String location,
            @RequestParam("cuisine") String cuisine) throws Exception{

        // Set up url
        String url = "https://api.yelp.com/v3/businesses/search?location=" + location + "&cuisine=" + cuisine;
        return getMapping(url);
    }

    /**
     * Method that searches restaurants based on type of cuisine, must select from the list of cuisines available in the yelp API
     * Due to the API's limits we can only return restaurants that currently have a review
     * @param cuisine
     * @param longitude
     * @param latitude
     * @return
     * @throws Exception
     */
    @GetMapping("/cuisine/longitude/latitude/")
    public ResponseEntity<String> searchByCuisine (
            @RequestParam("longitude") String longitude,
            @RequestParam("latitude") String latitude,
            @RequestParam("cuisine") String cuisine) throws Exception{

        // Set up url
        String url = "https://api.yelp.com/v3/businesses/search?longitude=" + longitude + "&latitude=" + latitude + "&cuisine=" + cuisine;
        return getMapping(url);
    }

    /**
     * Method that searches restaurants using google API
     *
     * @param location
     * @return ResponseEntity
     * @throws Exception
     */
    @GetMapping("/google/{location}/")
    public ResponseEntity<String> searchGoogle(
            @PathVariable("location") String location) {

        // Set up url
        String query = "restaurants+in+" + location;
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + query + "&key=" + googleApiKey;

        HttpEntity<Void> entity = null;

        // Response
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response;
    }

    /**
     * Method that searches restaurant and sort them by best_match, rating, review_count or distance
     * If recommend param is set to 1, it will return random restaurant from the result
     *
     * @param location
     * @param sortby:    best_match, rating, review_count or distance
     * @param recommend: 1-> True, 0-> False
     * @return ResponseEntity
     * @throws Exception
     */
    @GetMapping("/{location}/{sortby}/{recommend}/")
    public ResponseEntity<String> searchSortByDistance(
            @PathVariable("location") String location,
            @PathVariable("sortby") String sortby,
            @PathVariable("recommend") int recommend) throws Exception {
        // Set up url
        String url = null;
        String extraParam = "";

        if (recommend == 1) {
            Random rand = new Random();
            int randomOffset = rand.nextInt(50);
            String offset = Integer.toString(randomOffset);
            extraParam = extraParam + "&offset=" + offset + "&limit=1";
        }

        url = "https://api.yelp.com/v3/businesses/search?location=" + location + "&sort_by=" + sortby + extraParam;

        return getMapping(url);
    }

    /**
     * Method that searches restaurant and sort them by best_match, rating, review_count or distance
     * If recommend param is set to 1, it will return random restaurant from the result
     *
     * @param sortby:    best_match, rating, review_count or distance
     * @param recommend: 1-> True, 0-> False
     * @return ResponseEntity
     * @throws Exception
     */
    @GetMapping("/{sortby}/{recommend}/")
    public ResponseEntity<String> searchByLongLat(
            @RequestParam("long") String longitude,
            @RequestParam("lat") String latitude,
            @PathVariable("sortby") String sortby,
            @PathVariable("recommend") int recommend) throws Exception {
        // Set up url
        String url = null;
        String extraParam = "";

        if (recommend == 1) {
            Random rand = new Random();
            int randomOffset = rand.nextInt(50);
            String offset = Integer.toString(randomOffset);
            extraParam = extraParam + "&offset=" + offset + "&limit=1";
        }

        url = "https://api.yelp.com/v3/businesses/search?longitude=" + longitude
                    + "&latitude=" + latitude
                    + "&sort_by=" + sortby + extraParam;

        return getMapping(url);
    }

    /**
     * Method that searches restaurant based on price range. Must include either location or longitude and latitude.
     *
     * @param location
     * @param price
     * @return
     * @throws Exception
     */
    @GetMapping("/price/")
    public ResponseEntity<String> searchByPriceRange(
            @RequestParam("location") String location,
            @RequestParam("price") String price) throws Exception {

        // Set up url
        String url = "https://api.yelp.com/v3/businesses/search?location=" + location + "&price=" + price;

        return getMapping(url);
    }

    /**
     * Method that searches restaurant based on price range. Must include either location or longitude and latitude.
     *
     * @param longitude
     * @param latitude
     * @param price
     * @return
     * @throws Exception
     */
    @GetMapping("/price/longitude/latitude/")
    public ResponseEntity<String> searchByPriceRange(
            @RequestParam("longitude") String longitude,
            @RequestParam("latitude") String latitude,
            @RequestParam("price") String price) throws Exception {

        // Set up url
        String url = "https://api.yelp.com/v3/businesses/search?longitude=" + longitude + "&latitude=" + latitude + "&price=" + price;

        return getMapping(url);
    }
    
    //TODO Marine
    @GetMapping("/businesses/{id}/closing")
    public ResponseEntity<String> isRestaurantClosingInOneHour(@RequestParam("id")String id){
        // Set up url
        String url = "https://api.yelp.com/v3/businesses/" + id;
        try {
			ResponseEntity<String> answer = getMapping(url);
			answer.getBody();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return new ResponseEntity<String>("The restaurant is closing in less than 1 hour.", HttpStatus.OK);

    	//want this type of answer with true or false
    	//{\"response\":true,\"message\":\"User account successfully deleted.\"}
    	
    			//ResponseEntity.ok(gson.ToJson(""));
    			//ResponseEntity.status(HttpStatus.OK).body(new Response(true, "The restaurant is closing in less than 1 hour."));
    }
}
