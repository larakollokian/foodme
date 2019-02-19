package ca.mcgill.ecse428.foodme.controller;

import ca.mcgill.ecse428.foodme.model.AppUser;
import ca.mcgill.ecse428.foodme.model.Preference;
import ca.mcgill.ecse428.foodme.repository.FoodmeRepository;
import ca.mcgill.ecse428.foodme.repository.InvalidInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class Controller 
{
	@Autowired
	FoodmeRepository repository;
	String APIKey = "F5ByVWSif5NWb6w3YYAQjRGOI9Xcg8WKqzBDkPnEl4YDneNpsaKn35YcFEqJyvyV_kUTStuTG2n9-Pi9R7-u9GIkmBQY8LjfNJSrAVEs_K5pGJLCAsWc4N3oxGRgXHYx";

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
							if (username.length() >= 1) {
								u = repository.createAccount(username, firstName, lastName, email, password);
							} else {
								throw new InvalidInputException("Your username must have at least 1 character!");
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

	@GetMapping("/search/price/")
	public ResponseEntity<String> searchByPriceRange (
	        @RequestParam("location") String location,
            @RequestParam("price") String price) {

	    String url = "https://api.yelp.com/v3/businesses/search?location=" + location + "&price=" + price;

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + APIKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        entity.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////                                                                   /////////////////
	/////////////////                   PREFERENCE CONTROLLER                           /////////////////
	/////////////////                                                                   /////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	@PostMapping("/users/{user}/preferences/")
	public Preference addPreference(
			@PathVariable("user") String username, @RequestParam String priceRange, @RequestParam String distanceRange,
			@RequestParam String cuisine, @RequestParam String rating) {

		AppUser appUser = repository.getAppUser(username);
		Preference preference =  repository.createPreference(appUser, priceRange, distanceRange, cuisine, rating);
		return preference;
	}

	@PostMapping("/users/{user}/preferences/{pID}/")
	public Preference editPreference(
			@PathVariable("user") String username, @PathVariable("pID") int pID, @RequestParam String priceRange,
			@RequestParam String distanceRange, @RequestParam String cuisine, @RequestParam String rating){

		AppUser appUser = repository.getAppUser(username);
		List<Preference> preferenceList = appUser.getPreferences();
		Preference editPreference = repository.getPreference(pID);
		int index = preferenceList.indexOf(editPreference);

		editPreference = repository.editPreference(appUser, editPreference, priceRange, distanceRange, cuisine, rating, index);
		return editPreference;
	}
}
