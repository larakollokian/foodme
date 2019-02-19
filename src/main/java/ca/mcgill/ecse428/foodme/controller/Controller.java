package ca.mcgill.ecse428.foodme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse428.foodme.model.*;
import ca.mcgill.ecse428.foodme.repository.FoodmeRepository;
import ca.mcgill.ecse428.foodme.service.*;

@RestController
public class Controller 
{
	@Autowired
	FoodmeRepository repository;

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
	
}
