package ca.mcgill.ecse428.foodme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ca.mcgill.ecse428.foodme.model.*;
import ca.mcgill.ecse428.foodme.repository.FoodmeRepository;

import java.util.List;

@RestController
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
	
	@PostMapping("/users/create/{username}/{firstName}/{lastName}/{email}/{password}")
	public AppUser testCreateUser(@PathVariable("username")String username, @PathVariable("firstName")String firstName, 
			@PathVariable("lastName")String lastName, @PathVariable("email")String email, @PathVariable("password")String password)
	{
		AppUser u = repository.testCreateUser(username, firstName, lastName, email, password);
		return u;
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
