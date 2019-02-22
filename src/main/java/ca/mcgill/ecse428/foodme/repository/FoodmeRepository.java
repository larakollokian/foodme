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

	@Transactional
	public Preference createPreference(AppUser user, String priceRange, String distanceRange, String cuisine, String rating){
		Preference preference = new Preference();
		preference.setPrice(PriceRange.valueOf(priceRange));
		preference.setDistance(DistanceRange.valueOf(distanceRange));
		preference.setCuisine(Cuisine.valueOf(cuisine));
		preference.setRating(Rating.valueOf(rating));
		preference.setUser(user);
		user.addPreference(preference);
		entityManager.persist(preference);
		entityManager.merge(user);
		return preference;
	}

	@Transactional
	public Preference editPreference(AppUser user, Preference editPreference, String priceRange, String distanceRange,
									 String cuisine, String rating, int index) {
		editPreference.setPrice(PriceRange.valueOf(priceRange));
		editPreference.setDistance(DistanceRange.valueOf(distanceRange));
		editPreference.setCuisine(Cuisine.valueOf(cuisine));
		editPreference.setRating(Rating.valueOf(rating));
		user.getPreferences().set(index, editPreference);
		entityManager.merge(editPreference);
		entityManager.merge(user);
		return editPreference;
	}

	@Transactional
	public AppUser getAppUser(String username){
		AppUser appUser = entityManager.find(AppUser.class, username);
		return appUser;
	}

	@Transactional
	public Preference getPreference(int pID){
		Preference preference = entityManager.find(Preference.class, pID);
		return preference;
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
	public AppUser changePassword(String username, String newPassword) {
		AppUser u = entityManager.find(AppUser.class, username);
		u.setPassword(newPassword);
		entityManager.merge(u);
		return u;
		
	}
	

	/**
	 * Method that allows users to delete their account
	 * @param aUser
	 */
	@Transactional
	public void deleteUser(String username) throws ParseException {
		AppUser u = entityManager.find(AppUser.class, username);
		entityManager.remove(u);
		
		// aUser.setUsername(null);
		// aUser.setLikes(null);
		// aUser.setDislikes(null);
		// aUser.setEmail(null);
		// aUser.setFirstName(null);
		// aUser.setLastName(null);
		// aUser.setPassword(null);
	}


	/**
	 * Method that checks to see if a restaurant is open at the current time 
	 * @param aRestaurant
	 * @return
	 */
	// public boolean isRestaurantOpen(Restaurant aRestaurant) {

	// 	Date date = new Date();
	// 	Date time = new Date();

	// 	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	// 	Date dateInput = sdf.parse(departureDate);
	// 	String date1 = sdf.format(dateInput);        
	// 	String date2 = sdf.format(date);

	// 	SimpleDateFormat sdf2 = new SimpleDateFormat("HHmm");
	// 	Date timeInput = sdf2.parse(departureTime);
	// 	String time1 = sdf2.format(timeInput);
	// 	String time2 = sdf2.format(time);
		
	// 	return true;
	// }

}
