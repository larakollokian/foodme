package ca.mcgill.ecse428.foodme.repository;


import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ca.mcgill.ecse428.foodme.model.*;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;


@Repository
public class FoodmeRepository {
	
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public AppUser testCreateUser(String username, String firstName, String lastName, String email, String password) 
	{
		AppUser u = new AppUser();
		u.setUsername(username);
		u.setFirstName(firstName);
		u.setLastName(lastName);
		u.setEmail(email);
		u.setPassword(password);
		u.setLikes(new ArrayList<String>());
		u.setDislikes(new ArrayList<String>());
		entityManager.persist(u);
		return u;
	}

//	public Restaurant restaurant;
//	public User user;
//	private List<Restaurant> liked;
//	
//	/**
//	 * Method to like a restaurant so its in the user list of liked restaurant
//	 * @param restaurant The restaurant a user likes
//	 * @return void The method returns nothing, this change will be saved in the database
//	 */
//	public void isLiked(Restaurant restaurant) {
//		liked=user.getLiked();
//		liked.add(restaurant);
//		user.setLiked(liked);
//	}
//	
//	/**
//	 * Method to list all the liked restaurants of a user
//	 * @return The list of all the liked restaurants
//	 */
//	public List<Restaurant> listAllLiked() {
//		List<Restaurant> liked = user.getLiked();
//		return liked;
//	}
	

/**
	 * Method that allows users to update their account's password
	 * @param aUser
	 * @param newPassword
	 */
	public void changePassword(AppUser aUser, String newPassword) {
		aUser.setPassword(newPassword);
		return;
		
	}
	

	/**
	 * Method that allows users to delete their account
	 * @param aUser
	 */
	public void deleteAccount(AppUser aUser) {
		aUser.setLikes(null);
		aUser.setDislikes(null);
		aUser.setEmail(null);
		aUser.setFirstName(null);
		aUser.setLastName(null);
		aUser.setPassword(null);
	}


	/**
	 * Method that checks to see if a restaurant is open at the current time 
	 * @param aRestaurant
	 * @return
	 */
	public boolean isRestaurantOpen(Restaurant aRestaurant) {

		Date date = new Date();
		Date time = new Date();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date dateInput = sdf.parse(departureDate);
		String date1 = sdf.format(dateInput);        
		String date2 = sdf.format(date);

		SimpleDateFormat sdf2 = new SimpleDateFormat("HHmm");
		Date timeInput = sdf2.parse(departureTime);
		String time1 = sdf2.format(timeInput);
		String time2 = sdf2.format(time);
		
		return true;
	}

}
