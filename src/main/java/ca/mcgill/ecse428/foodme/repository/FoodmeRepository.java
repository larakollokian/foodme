package ca.mcgill.ecse428.foodme.repository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import ca.mcgill.ecse428.foodme.model.*;

import ca.mcgill.ecse428.foodme.security.Password;
import ca.mcgill.ecse428.foodme.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;


//@SuppressWarnings("Duplicates")
@Repository
public class FoodmeRepository {
	
	@PersistenceContext
	private EntityManager entityManager;

//	String APIKey = "F5ByVWSif5NWb6w3YYAQjRGOI9Xcg8WKqzBDkPnEl4YDneNpsaKn35YcFEqJyvyV_kUTStuTG2n9-Pi9R7-u9GIkmBQY8LjfNJSrAVEs_K5pGJLCAsWc4N3oxGRgXHYx";
//
//	@Autowired
//	AuthenticationService authentication;
//
//	@Autowired
//	public FoodmeRepository(){}

	@Transactional
	public AppUser testCreateUser(String username, String firstName, String lastName, String email, String password)
	{
		AppUser u = new AppUser();
		u.setUsername(username);
		u.setFirstName(firstName);
		u.setLastName(lastName);
		u.setEmail(email);
		u.setPassword(password);
		u.setPreferences(new ArrayList<Preference>());
		u.setLikesAnsDislikes(new ArrayList<Restaurant>());
		entityManager.persist(u);
		return u;
	}

	/**
	 * Method to create an new account
	 * @param username The user's chosen username
	 * @param firstName The user's first name
	 * @param lastName The user's last name
	 * @param email The user's email address
	 * @param password The user's password
	 * @return User entity that was created
	 */
	@Transactional
	public AppUser createAccount (String username, String firstName, String lastName, String email, String password) throws InvalidInputException {

		AppUser u = new AppUser();
		u.setUsername(username);
		u.setFirstName(firstName);
		u.setLastName(lastName);
		u.setEmail(email);

		String passwordHash="";

		try {
			passwordHash = Password.getSaltedHash(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		u.setPassword(passwordHash);
		u.setLikesAnsDislikes(new ArrayList<Restaurant>());
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
		entityManager.persist(preference);
		user.addPreference(preference);
		entityManager.merge(user);
		return preference;
	}

	@Transactional
	public Preference editPreference(Preference editPreference, String priceRange, String distanceRange,
									 String cuisine, String rating) {
		editPreference.setPrice(PriceRange.valueOf(priceRange));
		editPreference.setDistance(DistanceRange.valueOf(distanceRange));
		editPreference.setCuisine(Cuisine.valueOf(cuisine));
		editPreference.setRating(Rating.valueOf(rating));
		entityManager.merge(editPreference);
		return editPreference;
	}

	@Transactional
	public AppUser getAppUser(String username){

		if(entityManager.find(AppUser.class, username) == null) {
			System.out.println("This user does not exist");
		}
		else {
		AppUser appUser = entityManager.find(AppUser.class, username);
		return appUser;
		}
		return null;
	}

	/**
	 * gets all users in database using native SQL query statements
	 * @return list of AppUsers
	 */
	@Transactional
	public List<AppUser> getAllUsers()
	{
		Query q = entityManager.createNativeQuery("SELECT * FROM app_user");
		@SuppressWarnings("unchecked")
		List<AppUser> users = q.getResultList();
		return users;
	}
	/**
	 *gets number of users
	 * @return number of users
	 */
	@Transactional
	public int getNumberUsers(){
		return getAllUsers().size();
	}

	@Transactional
	public Preference getPreference(int pID){
		Preference preference = entityManager.find(Preference.class, pID);
		return preference;
	}
	
	/**
	 * Method to like a restaurant so its in the user list of liked restaurant
	 * @param restaurant The restaurant a user likes
	 * @return void The method returns nothing, this change will be saved in the database
	 */
	@Transactional
	public void addLiked(String username, String restaurantName) {
		AppUser appUser = entityManager.find(AppUser.class, username);
		Restaurant restaurant = entityManager.find(Restaurant.class, restaurantName);
		restaurant.setLiked(true);
		restaurant.setRestaurantName(restaurantName);
		restaurant.setAppUser(appUser);
		entityManager.merge(restaurant);
		//entityManager.merge(appUser);
	}

	/**
	 * Method to list all the liked restaurants of a user
	 * @return The list of all the liked restaurants
	 */
	public List<Restaurant> listAllLiked(String username) {
		AppUser appUser = entityManager.find(AppUser.class, username);

		//TODO change the query to what is in the db
		Query q = entityManager.createNativeQuery("SELECT FROM restaurant WHERE app_user= :user and liked=true");
		q.setParameter("user", username);
		@SuppressWarnings("unchecked")
		List<Restaurant>likedAndDisliked = q.getResultList();
		//filter through that list for only liked and not disliked either from db or here.
		if(likedAndDisliked.isEmpty()) {
			return Collections.emptyList();
		}

		List<Restaurant> liked = new ArrayList<Restaurant>();
		for(Restaurant r: likedAndDisliked) {
			if(r.isLiked()) {
				liked.add(r);
			}
		}
		if(liked.isEmpty()) {
			return Collections.emptyList();
		}

		return liked;
	}
	

	/**
	 * Method that allows users to update their account's password
	 * @param username
	 * @param newPassword
	 */


	@Transactional
	public AppUser changePassword(String username, String newPassword) {
		AppUser u = entityManager.find(AppUser.class, username);
		u.setPassword(newPassword);
		entityManager.merge(u);
		return u;
	}

	/**
	 * Method that allows users to delete their account
	 * @param username
	 */
	@Transactional
	public void deleteUser(String username) /*throws ParseException*/ {

		if(entityManager.find(AppUser.class, username) == null) {
			System.out.println("Cannot delete a user that does not exist");
		}
		else {
		AppUser u = entityManager.find(AppUser.class, username);
		entityManager.remove(u);
		//entityManager.detach(u);
		}
	}

	/**
	 * gets all preferences in database regardless of user
	 * @return
	 */
	@Transactional
	public List<Preference> getAllPreferences() 
	{
		Query q = entityManager.createNativeQuery("SELECT * FROM preference");
		@SuppressWarnings("unchecked")
		List<Preference> preferences = q.getResultList();
		return preferences;
	}

	/**
	 * getting the paramaters for a specific user
	 * @param username
	 * @return list of parameters
	 */
	@Transactional
	public List<Preference> getPreferencesForUser(String username) 
	{
		Query q = entityManager.createNativeQuery("SELECT * FROM preference WHERE app_user= :user");
		q.setParameter("user", username);
		@SuppressWarnings("unchecked")
		List<Preference> preferences = q.getResultList();
		return preferences;
	}

	/**
	 * Method that checks to see if a restaurant is open at the current time 
	 * @param
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

// TODO test method when not too many connections
//	public ResponseEntity<List> getAllRestaurants(String location) throws InvalidInputException{
//
//		String url = null;
//		if (location != null) {
//			url = "https://api.yelp.com/v3/businesses/search?location=" + location;
//		} else {
//			return null;
//		}
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Authorization", "Bearer " + APIKey);
//		headers.setContentType(MediaType.APPLICATION_JSON);
//
//		HttpEntity<Void> entity = new HttpEntity<>(headers);
//
//		// Response
//		RestTemplate restTemplate = new RestTemplate();
//		ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
//
//		return response;
//
//	}

// TODO test method when not too many connections
//	public Object getRestaurant(String id){
//		String url = null;
//		if (location != null) {
//			url = "https://api.yelp.com/v3/businesses/" + id;
//		} else {
//			return null;
//		}
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Authorization", "Bearer " + APIKey);
//		headers.setContentType(MediaType.APPLICATION_JSON);
//
//		HttpEntity<Void> entity = new HttpEntity<>(headers);
//
//		// Response
//		RestTemplate restTemplate = new RestTemplate();
//		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
//
//		return response;
//	}

}
