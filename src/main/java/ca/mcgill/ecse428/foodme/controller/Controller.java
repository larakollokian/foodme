package ca.mcgill.ecse428.foodme.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import ca.mcgill.ecse428.foodme.repository.*;
import java.text.ParseException;
import java.util.*;

@RestController
@CrossOrigin
public class Controller 
{
	@Autowired
	FoodmeRepository repository;
	String APIKey = "F5ByVWSif5NWb6w3YYAQjRGOI9Xcg8WKqzBDkPnEl4YDneNpsaKn35YcFEqJyvyV_kUTStuTG2n9-Pi9R7-u9GIkmBQY8LjfNJSrAVEs_K5pGJLCAsWc4N3oxGRgXHYx";

	@Autowired 
	AuthenticationService authentication;


	/**
	 * Greeting on home page of the website
	 * @return hello world
	 */
	@RequestMapping("/")
	public String greeting()
	{
		return "Hello world!";
	}

	/**
	 * Name specific greeting
	 * @param name
	 * @return hello + name
	 */
	@PostMapping("/{name}")
	public String greeting(@PathVariable("name")String name)
	{
		if(name == null)
		{
			return "Hello world!";
		}
		else
		{
			return "Hello, " + name + "!";
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////                                                                   /////////////////
	/////////////////                     APP USER CONTROLLER                           /////////////////
	/////////////////                                                                   /////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	@PostMapping("/users/testCreate/{username}/{firstName}/{lastName}/{email}/{password}")
	public AppUser testCreateUser(@PathVariable("username")String username, @PathVariable("firstName")String firstName,
			@PathVariable("lastName")String lastName, @PathVariable("email")String email, @PathVariable("password")String password)
	{
		AppUser u = repository.testCreateUser(username, firstName, lastName, email, password);
		return u;
	}


	/* Attempts to login and returns the session if successful
	 * @param username
	 * @return sessionGuid
	 * @throws AuthenticationException
	 */
	@PostMapping(value = { "/login" })
	public String login(@RequestParam String username, @RequestParam String password) throws Exception {
		return authentication.login(username, password);
	}

	/**
	 * Method that creates a new account for a user. Username must be unique.
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
			@RequestParam("firstName")String firstName,
			@RequestParam("lastName")String lastName,
			@RequestParam("email")String email,
			@RequestParam("password")String password) throws InvalidInputException
	{
		AppUser u;

		try {
			if (email.contains("@") && email.contains(".")) {
				try {
					if (password.length() >= 6) {
						try {
							if(getAppUser(username) == null) {
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

		// if(u.getPassword() == oPassword) {
		// 	System.out.println("Error: New password cannot be the same as old password");
		// } 
		// else {
		u.setPassword(nPassword);
		return u;
	}

	// AppUser u = repository.getAppUser(username);
	// u.setPassword(password);
	// return;


	//}


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

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////                                                                   /////////////////
	/////////////////                  BUSINESS SEARCH CONTROLLER                       /////////////////
	/////////////////                                                                   /////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Method that searches restaurant based on price range. Must include either location or longitude and latitude.
	 * @param location
	 * @param price
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/search/price/")
	public ResponseEntity<String> searchByPriceRange (
			@RequestParam("location") String location,
			@RequestParam("price") String price) throws Exception{

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
	 * @param location
	 * @param price
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/search/price/")
	public ResponseEntity<String> searchByPriceRange (
			@RequestParam("longitude") String longitude,
			@RequestParam("latitude") String latitude,
			@RequestParam("price") String price) throws Exception{

		// Set up url
		String url = null;
		if (longitude != null && latitude != null) {
			url = "https://api.yelp.com/v3/businesses/search?longitude=" + longitude + "&latitude=" + latitude + "&price=" + price;
		} else {
			throw new Exception("You are missing longitude & latitude to make a query!");
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
}
