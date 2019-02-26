package ca.mcgill.ecse428.foodme.controller;

import ca.mcgill.ecse428.foodme.service.InvalidSessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import ca.mcgill.ecse428.foodme.model.*;
import ca.mcgill.ecse428.foodme.service.AuthenticationException;
import ca.mcgill.ecse428.foodme.service.AuthenticationService;
import ca.mcgill.ecse428.foodme.security.Password;
import ca.mcgill.ecse428.foodme.repository.*;
import java.text.ParseException;
import java.util.*;

@RestController
@CrossOrigin
public class Controller {
    @Autowired
    FoodmeRepository repository;

    String APIKey = "F5ByVWSif5NWb6w3YYAQjRGOI9Xcg8WKqzBDkPnEl4YDneNpsaKn35YcFEqJyvyV_kUTStuTG2n9-Pi9R7-u9GIkmBQY8LjfNJSrAVEs_K5pGJLCAsWc4N3oxGRgXHYx";
    String googleApiKey = "AIzaSyAbDiuDSRG-3oyFUzlS0SOy1g5b0n49dus";

    @Autowired
    AuthenticationService authentication;
//
//	@Autowired
//	public Controller(){}


    /**
     * Greeting on home page of the website
     *
     * @return hello world
     */
    @RequestMapping("/")
    public String greeting() {
        return "Hello world!";
    }

    /**
     * Name specific greeting
     *
     * @param name
     * @return hello + name
     */
    @PostMapping("/{name}")
    public String greeting(@PathVariable("name") String name) {
        if (name == null) {
            return "Hello world!";
        } else {
            return "Hello, " + name + "!";
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////                                                                   /////////////////
    /////////////////                     APP USER CONTROLLER                           /////////////////
    /////////////////                                                                   /////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @PostMapping("/users/testCreate/{username}/{firstName}/{lastName}/{email}/{password}")
    public AppUser testCreateUser(@PathVariable("username") String username, @PathVariable("firstName") String firstName,
                                  @PathVariable("lastName") String lastName, @PathVariable("email") String email, @PathVariable("password") String password) {
        AppUser u = repository.testCreateUser(username, firstName, lastName, email, password);
        return u;
    }


    /* Attempts to login and returns the session if successful
	 * @param username
	 * @param password
	 * @return TRUE if the account is authenticated
	 */
	@GetMapping("/users/auth/{username}/{password}")
	public Response login(@PathVariable("username")String username, @PathVariable("password")String password) throws Exception {
        //No exception thrown means the authentication succeeded
        Response r = new Response();
        try {
			authentication.login(username, password);
		}
        catch(AuthenticationException e){
        	r.setResponse(false);
        	r.setError("Invalid Password");
        	return r;
		}
        catch(InvalidSessionException e){
        	r.setResponse(false);
        	r.setError("User does not exist");
        	return r;
		}
	    r.setResponse(true);
	    r.setError(null);
	    return r;
	}

    /**
     * Method that creates a new account for a user. Username must be unique.
     *
     * @param username
     * @param firstName
     * @param lastName
     * @param email
     * @param password
     * @return The new user
     * @throws InvalidInputException
     */
    @PostMapping("/users/create/{username}/")
    public AppUser createAccount(
            @PathVariable("username") String username,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("password") String password) throws InvalidInputException {
        AppUser u;

        try {
            if (email.contains("@") && email.contains(".")) {
                try {
                    if (password.length() >= 6) {
                        try {
                            if (getAppUser(username) == null) {
                                if (username.length() >= 1) {
                                    u = repository.createAccount(username, firstName, lastName, email, password);
                                } else {
                                    throw new InvalidInputException("Your username must have at least 1 character!");
                                }
                            } else {
                                throw new InvalidInputException("This username already exists!");
                            }
                        } catch (NullPointerException e) {
                            throw new InvalidInputException("Please enter a username");
                        }
                    } else {
                        throw new InvalidInputException("Your password must be longer than 6 characters!");
                    }
                } catch (NullPointerException e) {
                    throw new InvalidInputException("Please enter a password");
                }
            } else {
                throw new InvalidInputException("This is not a valid email address!");
            }
        } catch (NullPointerException e) {
            throw new InvalidInputException("This is not a valid email address!");
        }

        return u;
    }

    /**
     * Method that searches restaurant based on price range. Must include either location or longitude and latitude.
     *
     * @param location
     * @param price
     * @return
     * @throws Exception
     */
    @GetMapping("/search/price/")
    public ResponseEntity<String> searchByPriceRange(
            @RequestParam("location") String location,
            @RequestParam("price") String price) throws Exception {

        // Set up url
        String url = null;
        if (location != null) {
            url = "https://api.yelp.com/v3/businesses/search?location=" + location + "&price=" + price;
        } else {
            throw new Exception("You are missing a location to make a query!");
        }

        // Add headers (e.g. Authentication for Yelp Fusion API access)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + APIKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Response
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response;
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
    @GetMapping("/search/price/longitude/latitude/")
    public ResponseEntity<String> searchByPriceRange(
            @RequestParam("longitude") String longitude,
            @RequestParam("latitude") String latitude,
            @RequestParam("price") String price) throws Exception {

        // Set up url
        String url = null;
        if (longitude != null && latitude != null) {
            url = "https://api.yelp.com/v3/businesses/search?longitude=" + longitude + "&latitude=" + latitude + "&price=" + price;
        } else {
            throw new Exception("You are missing a location to make a query!");
        }

        // Add headers (e.g. Authentication for Yelp Fusion API access)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + APIKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

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
    @GetMapping("/search/{location}/{sortby}/{recommend}/")
    public ResponseEntity<String> searchSortByDistance(
            @PathVariable("location") String location,
            @PathVariable("sortby") String sortby,
            @PathVariable("recommend") int recommend) throws Exception {
        // Set up url
        String url = null;
        String extraParam = "";
        if (location != null) {
            if (recommend == 1) {
                Random rand = new Random();
                int randomOffset = rand.nextInt(50);
                String offset = Integer.toString(randomOffset);
                extraParam = extraParam + "&offset=" + offset + "&limit=1";
            }
            url = "https://api.yelp.com/v3/businesses/search?location=" + location + "&sort_by=" + sortby + extraParam;

        } else {
            throw new Exception("You are missing a location to make a query!");
        }

        // Add headers (e.g. Authentication for Yelp Fusion API access)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + APIKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Response
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response;
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
    @PostMapping("/search/{sortby}/{recommend}/")
    public ResponseEntity<String> searchByLongLat(
    		@RequestParam("long") String longitude,
    		@RequestParam("lat") String latitude,
            @PathVariable("sortby") String sortby,
            @PathVariable("recommend") int recommend) throws Exception {
        // Set up url
        String url = null;
        String extraParam = "";
        if (longitude != null && latitude != null) {
            if (recommend == 1) {
                Random rand = new Random();
                int randomOffset = rand.nextInt(50);
                String offset = Integer.toString(randomOffset);
                extraParam = extraParam + "&offset=" + offset + "&limit=1";
            }
            url = "https://api.yelp.com/v3/businesses/search?longitude=" + longitude 
            							+ "&latitude=" + latitude 
            							+ "&sort_by=" + sortby + extraParam;

        } else {
            throw new Exception("You are missing a location to make a query!");
        }

        // Add headers (e.g. Authentication for Yelp Fusion API access)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + APIKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Response
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response;
    }


    /**
     * Method that searches restaurants using google API
     *
     * @param location
     * @return ResponseEntity
     * @throws Exception
     */
    @GetMapping("/search/google/{location}/")
    public ResponseEntity<String> searchGoogle(
            @PathVariable("location") String location) throws Exception {
        // Set up url
        String url = null;
        if (location != null) {
            String query = "restaurants+in+" + location;
            url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + query + "&key=" + googleApiKey;

        } else {
            throw new Exception("You are missing a location to make a query!");
        }

		HttpEntity<Void> entity = null;

		// Response
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		
		return response;
	}
	
	/**
	 * get user with username from database
	 * 
	 * @param username
	 * @return
	 */
	@GetMapping("/users/get/{username}")
	public AppUser getAppUser(@PathVariable("username")String username) 
	{
		AppUser u = repository.getAppUser(username);
		return u;
	}
	
	/**
	 * Method that searches restaurants based on type of cuisine, must select from the list of cuisines available in the yelp API 
	 * Due to the API's limits we can only return restaurants that currently have a review
	 * @param cuisine
	 * @param location
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/search/cuisine/")
	public ResponseEntity<String> searchByCuisine (
	        @RequestParam("location") String location,
            @RequestParam("cuisine") Cuisine cuisine) throws Exception{

		// Set up url
		String url = null;
		if (location != null) {
			url = "https://api.yelp.com/v3/businesses/search?location=" + location + "&cuisine=" + cuisine;
		} else {
			throw new Exception("You are missing a location to make a query!");
		}

		// Add headers (e.g. Authentication for Yelp Fusion API access)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + APIKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Response
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response;
	}


	/**
	 * Gets all users in the database. If there are none, returns an empty list
	 * @return list of users
	 */
	@GetMapping("/users/get/all")
	public List<AppUser> getAllUsers()
	{
		List<AppUser> allUsers = repository.getAllUsers();
		return allUsers;
	}

	/**
	 * delete user with username from database
	 * 
	 * @param username
	 */
	@PostMapping("/users/delete/{username}")
	public void deleteUser(@PathVariable("username")String username)
	{
		repository.deleteUser(username);
		
		// Response r = new Response();
		// try {
		// 	repository.deleteUser(username);
		// 	r.setResponse(true);

		// } catch (NullPointerException e) {
		// 	r.setResponse(false);
		// 	r.setError("No such User exists");
		// }
		// return r;	
	}

	@PostMapping("/users/changePassword/{username}/old/{oPassword}/new/{nPassword}")
	public AppUser changePassword(@PathVariable("username")String username,@PathVariable("oPassword")String oPassword, @PathVariable("nPassword")String nPassword) {


		AppUser u = repository.getAppUser(username);
		try {
			repository.changePassword(u.getUsername(),oPassword,nPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return u;
	}

	// AppUser u = repository.getAppUser(username);
	// u.setPassword(password);
	// return;


	//}
	@GetMapping("/password/random/{n}")
	public String getRandomPassword(@PathVariable("n") int length)
	{
		String randPassword = Password.generateRandomPassword(length);
		return randPassword;
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////                                                                   /////////////////
	/////////////////                   PREFERENCE CONTROLLER                           /////////////////
	/////////////////                                                                   /////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////


	@GetMapping("/preferences/get/all")
	public List<Preference> getAllPreferences()
	{
		List<Preference> allPs = repository.getAllPreferences();
		return allPs;
	}

	@GetMapping("/preferences/user/{username}")
	public List<Preference> getPreferencesForUser(@PathVariable("username") String username)
	{
		List<Preference> prefForUser = repository.getPreferencesForUser(username);
		return prefForUser;
	}

	@PostMapping("/users/{user}/preferences/")
	public Preference addPreference(
			@PathVariable("user") String username, @RequestParam String priceRange, @RequestParam String distanceRange,
			@RequestParam String cuisine, @RequestParam String rating) {

		AppUser appUser = repository.getAppUser(username);
		Preference preference = repository.createPreference(appUser, priceRange, distanceRange, cuisine, rating);
		return preference;
	}

	@PostMapping("/users/{user}/preferences/{pID}/")
	public Preference editPreference(
			@PathVariable("user") String username, @PathVariable("pID") int pID, @RequestParam String priceRange,
			@RequestParam String distanceRange, @RequestParam String cuisine, @RequestParam String rating){

		Preference editPreference = repository.getPreference(pID);
		if(editPreference.getUser().getUsername().equals(username))
		{
			editPreference = repository.editPreference(editPreference, priceRange, distanceRange, cuisine, rating);
		}
		else
		{
			System.out.println("The preference ID provided is not associated to this user");
		}
		return editPreference;
	}
	
	/**
	 * Method that deletes a preference
	 * @param pID
	 * @param username
	 * @return Preference
	 */
	@PostMapping("/users/{user}/preferences/delete/{pID}")
	public Preference deletePreference(
			@PathVariable("user") String username, @PathVariable("pID") int pID){

		Preference deletedPreference = repository.getPreference(pID);
		if(deletedPreference.getUser().getUsername().equals(username))
		{
			deletedPreference = repository.deletePreference(pID);
		}
		else
		{
			System.out.println("The preference ID provided is not associated to this user");
		}
		return deletedPreference;
	}
	
	/**
	 * Method that gets a default preference
	 * @param username
	 * @return Preference
	 */
	@GetMapping("/preferences/user/{username}/getDefault")
	public Preference getDefaultPreferences(@PathVariable("username") String username)
	{
		return repository.getDefaultPreference(username);
	}

	/**
	 * Method that sets a default preference
	 * @param pID
	 * @param username
	 * @return Preference
	 */
	@PostMapping("/preferences/user/{username}/setDefault/{pid}")
	public Preference setDefaultPreferences(@PathVariable("username") String username, @PathVariable("pid") int pID)
	{
		return repository.setDefaultPreference(pID,username);
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////                                                                   /////////////////
	/////////////////                        LIKED CONTROLLER                           /////////////////
	/////////////////                                                                   /////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Controller Method that takes a user and the ID of the restaurants they liked to add it in their liked restaurants
	 * @param username of the user on the application
	 * @param id of the restaurant
	 */
	@PostMapping("/users/{user}/liked/{id}")
	public void addLiked(@PathVariable("user") String username, @PathVariable("id") String id) {
		repository.addLiked(username, id);
	}
	
	/**
	 * Controller Method that takes a user and list all its liked restaurants
	 * @param username
	 * @return
	 */
	@PostMapping("/users/{user}/allliked/")
	public List<Restaurant> allLiked(@PathVariable("user") String username){
		List<Restaurant> liked = repository.listAllLiked(username);
		
//		for(String like: liked) {
//			ResponseEntity<String> likedRestaurant = lookUpRestaurantByID();
//		}
		return liked;
		
		
	}
	
	/**
	 * Controller method that calls the API to return a restaurant based on its id
	 * @param id
	 * @return Restaurant and all its information associated with it
	 * @throws Exception
	 */
	@GetMapping("/businesses/{id}")
	public ResponseEntity<String> lookUpRestaurantByID (@RequestParam("id") String id) throws Exception{

		// Set up url
		String url = null;
		if (id != null) {
			url = "https://api.yelp.com/v3/businesses/" + id;
		} else {
			throw new Exception("You are missing the id to make a query!");
		}

		// Add headers (e.g. Authentication for Yelp Fusion API access)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + APIKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Response
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response;
    }

    /**
     * Controller method to get all restaurants from a location
     * @param location
     * @return
     */
    @GetMapping("/restaurants/{location}")
    public String getAllRestaurants(@PathVariable("location") String location) {
        ResponseEntity<String> restaurants = null;
        try {
            restaurants = repository.getAllRestaurants(location);
        } catch (InvalidInputException e) {
            e.printStackTrace();
        }
        return restaurants.getBody();
    }

    /**
     * Controller method to get a restaurant's data based on its id
     * @param id
     * @return
     */
    	@GetMapping("/restaurant/{id}")
	public String getRestaurant(@PathVariable("id") String id)
	{
		ResponseEntity<String> restaurant = null;
		try {
			restaurant = repository.getRestaurant(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return restaurant.getBody();
	}
}