package ca.mcgill.ecse428.foodme.repository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import ca.mcgill.ecse428.foodme.model.*;

import ca.mcgill.ecse428.foodme.security.Password;
import org.springframework.http.*;
import ca.mcgill.ecse428.foodme.service.AuthenticationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;


//@SuppressWarnings("Duplicates")
@Repository
public class FoodmeRepository {
	
	@PersistenceContext
	private EntityManager entityManager;

	String APIKey = "F5ByVWSif5NWb6w3YYAQjRGOI9Xcg8WKqzBDkPnEl4YDneNpsaKn35YcFEqJyvyV_kUTStuTG2n9-Pi9R7-u9GIkmBQY8LjfNJSrAVEs_K5pGJLCAsWc4N3oxGRgXHYx";

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
	
	/**
	 * Method to delete a preference
	 * @param preference id
	 * @return the deleted preference
	 */
	@Transactional
	public Preference deletePreference(int Pid) {
		Preference p = entityManager.find(Preference.class, Pid);
		entityManager.remove(p);
		return p;
	}
	
	
	/**
	 * Method to set default preference
	 * @param id
	 * @param username
	 * @return the default preference
	 */
	@Transactional
	public Preference setDefaultPreference(int Pid,String username) {
		Preference olddp = getDefaultPreference(username);	
		
		if (olddp!=null) {
			olddp.setIsDefault(false); //model setter
			entityManager.merge(olddp);
		}
		
		Preference dp = entityManager.find(Preference.class, Pid);
		if (dp!=null) {
			dp.setIsDefault(true); //model setter
			entityManager.merge(dp);
			return dp;
		}
		return null;
	}
	
	/**
	 * Method to get default preference
	 * @param username
	 * @return preference
	 */
	@Transactional
	public Preference getDefaultPreference(String username) {
		
		Query q = entityManager.createNativeQuery("SELECT * FROM preference WHERE app_user= :user AND is_default= :default", Preference.class);
		q.setParameter("user", username);
		q.setParameter("default", true);
		@SuppressWarnings("unchecked")
		List<Preference> preferences = (List<Preference>) q.getResultList(); 
		if(preferences.size() != 0)
		{
			return preferences.get(0);
		}
		else
		{
			return null;
		}
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
	 * @param restaurantName The restaurant a user likes
	 * @return void The method returns nothing, this change will be saved in the database
	 */
	@Transactional
	public Restaurant addLiked(String username, String restaurantName) {
		AppUser appUser = getAppUser(username);

		Restaurant restaurant = entityManager.find(Restaurant.class, restaurantName);
		if(restaurant == null){
			restaurant = new Restaurant();
			restaurant.setRestaurantName(restaurantName);
			entityManager.persist(restaurant);
		}
		restaurant.setLiked(true);
		restaurant.setAppUser(appUser);
		entityManager.merge(restaurant);
		appUser.addLikesAnsDislike(restaurant);
		entityManager.merge(appUser);
		return restaurant;
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
	public AppUser changePassword(String username,String oldPassword, String newPassword) throws Exception {
		AppUser u = entityManager.find(AppUser.class, username);
		try{
			try {
				Password.check(oldPassword,u.getPassword());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		catch(Exception e){
			throw new AuthenticationException("Invalid Password");
		}
		u.setPassword(Password.getSaltedHash(newPassword));
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

	/**
	 * This is the method to get all the restaurants from a location (ie montreal)
	 * @param location
	 * @return
	 * @throws InvalidInputException
	 */
	public ResponseEntity<String> getAllRestaurants(String location) throws InvalidInputException{

		String url = null;
		if (location != null) {
			url = "https://api.yelp.com/v3/businesses/search?location=" + location;
		} else {
			return null;
		}

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
	 * This is the method to get a restaurant's info based on its ID
	 * @param id
	 * @return
	 */
	public ResponseEntity<String> getRestaurant(String id){
		String url = null;
		if (id != null) {
			url = "https://api.yelp.com/v3/businesses/" + id;
		} else {
			return null;
		}

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
