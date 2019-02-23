package ca.mcgill.ecse428.foodme.controller;
	
	
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;


import ca.mcgill.ecse428.foodme.model.*;
import ca.mcgill.ecse428.foodme.repository.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import java.util.List;

@RestController
@CrossOrigin
public class Controller 
{
	@Autowired
	FoodmeRepository repository;

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
	
	/**
	 * creates a new user with chosen parameters. Username must be unique. This is a test method that 
	 * doesn't include any parameter validation.
	 * 
	 * @param username
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param password
	 * @return the created user
	 */
	@PostMapping("/users/create/{username}/{firstName}/{lastName}/{email}/{password}")
	public AppUser testCreateUser(@PathVariable("username")String username, @PathVariable("firstName")String firstName, 
			@PathVariable("lastName")String lastName, @PathVariable("email")String email, @PathVariable("password")String password)
	{
		AppUser u = repository.testCreateUser(username, firstName, lastName, email, password);
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

	@PostMapping("/users/delete/{username}")
	public void deleteUser(@PathVariable("username")String username)
	{
		try {
			repository.deleteUser(username);
		} catch (ParseException e) {
			e.printStackTrace();
		}	
	}

	/**
	 * changes password for username
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@PostMapping("/users/changePassword/{username}/{password}")
	public AppUser changePassword(@PathVariable("username")String username,@PathVariable("password")String password) {
		AppUser u = repository.changePassword(username,password);
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
	public Preference createPreference(
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
}
