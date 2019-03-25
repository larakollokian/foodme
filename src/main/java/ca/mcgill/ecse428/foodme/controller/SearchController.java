package ca.mcgill.ecse428.foodme.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import ca.mcgill.ecse428.foodme.model.Response;

import java.util.Random;

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
        String url = "https://api.yelp.com/v3/businesses/search?location=" + location + "&categories=" + cuisine;
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
        String url = "https://api.yelp.com/v3/businesses/search?longitude=" + longitude + "&latitude=" + latitude + "&categories=" + cuisine;
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

        url = "https://api.yelp.com/v3/businesses/search?term=restaurants&location=" + location + "&sort_by=" + sortby + extraParam;

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
            @RequestParam("longitude") String longitude,
            @RequestParam("latitude") String latitude,
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

        url = "https://api.yelp.com/v3/businesses/search?term=restaurants&longitude=" + longitude
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
        String url = "https://api.yelp.com/v3/businesses/search?term=restaurants&location=" + location + "&price=" + price;

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
        String url = "https://api.yelp.com/v3/businesses/search?term=restaurants&longitude=" + longitude + "&latitude=" + latitude + "&price=" + price;

        return getMapping(url);
    }

    /**
     * Method that searches restaurant and sort them by best_match, rating, review_count or distance
     *
     * @param location
     * @param radius in meters
     * @param price
     * @param cuisine
     * @param sortby:    best_match, rating, review_count or distance
     * @return ResponseEntity
     * @throws Exception
     */
    @GetMapping("/filter/")
    public ResponseEntity<String> filterByPreference(
            @RequestParam("location") String location, 
            @RequestParam("radius") String radius,
            @RequestParam("price") String price, 
            @RequestParam("cuisine") String cuisine, 
            @RequestParam("sortby") String sortby) throws Exception {
        // Set up url
        String url = null;
        url = "https://api.yelp.com/v3/businesses/search?term=restaurants&location=" + location
                    + "&radius=" + radius + "&price=" + price + "&categories=" + cuisine 
                    + "&sort_by=" + sortby;

        return getMapping(url);
    }

/**
	 * Method that searches a restaurant closing hours and checks if the restaurant will be closing within one hour from now
	 * @param id id of the restaurant
	 * @return true when it is closing within one hour
	 * @return false when it is not closing within one hour
	 * @throws Exception
	 */
	@GetMapping("/get/closing")
	public ResponseEntity searchRestaurantClosingInOneHour(
			@RequestParam("id") String id) throws Exception {

		// Set up url
		String url = "https://api.yelp.com/v3/businesses/" + id;
		ResponseEntity<String> response = getMapping(url);
		String contentAsString = response.getBody();

		String endTime = "";
		String startTime = "";
		ZoneId easternZone = ZoneId.of("Canada/Eastern");
		LocalDate date = LocalDate.now(easternZone);
		LocalTime time = LocalTime.now(easternZone);
		System.out.println("the time is "+ time.toString());

		DayOfWeek dayOfWeek = date.getDayOfWeek();
		int dayOfWeekInInteger = 0;

		//convert to the day
		switch(dayOfWeek) {
		case MONDAY: dayOfWeekInInteger = 0; break;
		case TUESDAY: dayOfWeekInInteger = 1;break;
		case WEDNESDAY: dayOfWeekInInteger = 2;break;
		case THURSDAY: dayOfWeekInInteger = 3;break;
		case FRIDAY: dayOfWeekInInteger = 4;break;
		case SATURDAY: dayOfWeekInInteger = 5;break;
		case SUNDAY: dayOfWeekInInteger = 6;break;
		default: dayOfWeekInInteger = 0;break;
		}

		JSONObject json = new JSONObject(contentAsString);
		JSONArray hours = json.getJSONArray("hours");
		int length = hours.length();

		for(int i =0 ; i<length; i++){
			JSONObject allOpeningHours = hours.getJSONObject(i);
			JSONArray open = allOpeningHours.getJSONArray("open");
			int days = open.length();
			for(int j=0; j<days; j++) {
				JSONObject json3 = open.getJSONObject(j);
				int day = json3.getInt("day");
				System.out.println(json3.toString());

				//check if the same DAY in int
				if(dayOfWeekInInteger==day) {
					startTime = json3.getString("start");
					endTime = json3.getString("end");
					
					if(startTime.equals(endTime)) {
						return ResponseEntity.status(HttpStatus.OK).body(new Response(false, "The restaurant is still open."));
					}
					String hour = endTime.toString().substring(0, 2);
					String minute = endTime.toString().substring(2, 4);
					LocalTime restaurantTime = LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute));

					restaurantTime.minusHours(1);
					if(time.isAfter(restaurantTime)) {
						return ResponseEntity.status(HttpStatus.OK).body(new Response(true, "The restaurant is closing in less than 1 hour."));
					}
				}
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(new Response(false, "The restaurant is still open."));
	}
}
