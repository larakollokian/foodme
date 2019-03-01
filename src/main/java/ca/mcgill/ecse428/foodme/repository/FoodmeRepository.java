package ca.mcgill.ecse428.foodme.repository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import ca.mcgill.ecse428.foodme.model.*;

import ca.mcgill.ecse428.foodme.security.Password;
import ca.mcgill.ecse428.foodme.service.InvalidSessionException;
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
	public AppUser createAccount (String username, String firstName, String lastName, String email, String password){

		AppUser u = new AppUser();
		u.setUsername(username);
		u.setFirstName(firstName);
		u.setLastName(lastName);
		u.setEmail(email);
		//u.setDefaultPreferenceID(Integer.MIN_VALUE);

		String passwordHash="";
		try {
			passwordHash = Password.getSaltedHash(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		u.setPassword(passwordHash);
		entityManager.persist(u);
		return u;
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
            Password.check(oldPassword,u.getPassword());
        }
        catch(Exception e){
            throw new AuthenticationException("Invalid old password");
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
    public void deleteUser(String username) throws Exception {
        if(entityManager.find(AppUser.class, username) == null) {
            throw new Exception("Username " + username+ " does not exist");
        }
        else {
            AppUser u = entityManager.find(AppUser.class, username);
            entityManager.remove(u);
            //entityManager.detach(u);
        }
    }

    @Transactional
    public AppUser getAppUser(String username) throws InvalidSessionException {
        if(entityManager.find(AppUser.class, username) == null) {
            throw new InvalidSessionException("User does not exist");
        }
        else {
            AppUser appUser = entityManager.find(AppUser.class, username);
            return appUser;
        }
    }

    /**
     * gets all users in database using native SQL query statements
     * @return list of AppUsers
     */
    @Transactional
    public List<AppUser> getAllUsers()
    {
        Query q = entityManager.createNativeQuery("SELECT * FROM app_users");
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

    /**
	 * Method to set default preference
	 * @param pID
	 * @param username
	 * @return the default preference
	 */
	@Transactional
	public void setDefaultPreference(int pID, String username) throws Exception{
        AppUser user = null;
	    try {
             user = getAppUser(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Query q = entityManager.createNativeQuery("SELECT * FROM preferences WHERE app_user_username =:username AND pid =:pID");
        q.setParameter("username", username);
        q.setParameter("pID", pID);
        List<Preference> preferences = q.getResultList();
        if(preferences.size() == 1) {
            user.setDefaultPreferenceID(pID);
            entityManager.merge(user);
        }
        else{
            //then pid is not a preference of the appUser
            throw new Exception("The preference is not related to the user");
        }
    }

	/**
	 * Method to get default preference
	 * @param username
	 * @return preference
	 */
	@Transactional
	public Preference getDefaultPreference(String username) throws Exception {
        AppUser user = null;
        try {
            user = getAppUser(username);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int pID = user.getDefaultPreferenceID();

		Query q = entityManager.createNativeQuery("SELECT * FROM preferences WHERE app_user_username =:username AND pid =:pID");
        q.setParameter("username", username);
        q.setParameter("pID", pID);
        @SuppressWarnings("unchecked")
		List<Preference> preferences = q.getResultList();
		if(preferences.size() ==1 ) {
			return preferences.get(0);
		}
		else {
            throw new Exception("User does not have a default preference");
		}
	}

    /**
     * gets all preferences in database regardless of user
     * @return
     */
    @Transactional
    public List<Preference> getAllPreferences() {
        Query q = entityManager.createNativeQuery("SELECT * FROM preferences");
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
    public List<Preference> getPreferencesForUser(String username) {
        Query q = entityManager.createNativeQuery("SELECT * FROM preference WHERE app_user_username =: username");
        q.setParameter("username", username);
        @SuppressWarnings("unchecked")
        List<Preference> preferences = q.getResultList();
        return preferences;
    }

    @Transactional
    public Preference getPreference(int pID) throws Exception{
        if(entityManager.find(Preference.class, pID)==null){
            throw new Exception("Preference with pID "+pID+" does not exist");
        }
        Preference preference = entityManager.find(Preference.class, pID);
        return preference;
    }

	@Transactional
	public Preference createPreference(AppUser user, String location, String cuisine, String price, String sortBy){
		Preference preference = new Preference();
		preference.setLocation(location);
        preference.setCuisine(cuisine);
        preference.setPrice(price);
        preference.setSortBy(sortBy);
		preference.setUser(user);
		entityManager.persist(preference);
		return preference;
	}

	@Transactional
	public Preference editPreference(Preference editPreference, String location, String cuisine, String price, String sortBy) {
		editPreference.setLocation(location);
		editPreference.setCuisine(cuisine);
		editPreference.setPrice(price);
		editPreference.setSortBy(sortBy);
		entityManager.merge(editPreference);
		return editPreference;
	}

	/**
	 * Method to delete a preference
	 * @param  pID (preference ID)
	 * @return the deleted preference
	 */
	@Transactional
	public Preference deletePreference(int pID) {
		Preference p = entityManager.find(Preference.class, pID);
		entityManager.remove(p);
		return p;
	}

	public Restaurant createRestaurant(String restaurantID, String restaurantName){
		Restaurant restaurant = new Restaurant();
		restaurant.setRestaurantName(restaurantName);
		restaurant.setRestaurantID(restaurantID);
		entityManager.persist(restaurant);
		return restaurant;
	}
	/**
	 * Method to like a restaurant so its in the user list of liked restaurant
	 * @param restaurantName The restaurant a user likes
	 * @return void The method returns nothing, this change will be saved in the database
	 */
	@Transactional
	public Restaurant addLiked(String username, String restaurantID, String restaurantName) throws Exception {
        AppUser appUser = null;
        try {
            appUser = getAppUser(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Restaurant restaurant = entityManager.find(Restaurant.class, restaurantID);
		if(restaurant == null){
			createRestaurant(restaurantID,restaurantName);
		}
		//Check if restaurant is liked by user
		if(appUser.getDislikedRestaurants().contains(restaurant)){
			throw new Exception ("Restaurant is disliked");
		}
		else {
			appUser.addlikedRestaurants(restaurant);
			restaurant.addLikedAppUsers(appUser);
			entityManager.merge(appUser);
			entityManager.merge(restaurant);
			return restaurant;
		}
	}

	/**
	 * Method to list all the liked restaurants of a user
	 * @return The list of all the liked restaurants
	 */
	public List<String> listAllLiked(String username) {
		Query q = entityManager.createNativeQuery("SELECT restaurantid FROM liked_Restaurants WHERE username =:username");
		q.setParameter("username", username);
		@SuppressWarnings("unchecked")
		List<String>likedRestaurants = q.getResultList();
		return likedRestaurants;
	}

    /**
     * Method to like a restaurant so its in the user list of disliked restaurant
     * @param restaurantName The restaurant a user likes
     * @return void The method returns nothing, this change will be saved in the database
     */
    @Transactional
    public Restaurant addDisliked(String username, String restaurantID, String restaurantName) throws Exception {
        AppUser appUser = null;
        try {
            appUser = getAppUser(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Restaurant restaurant = entityManager.find(Restaurant.class, restaurantID);
        if(restaurant == null){
			createRestaurant(restaurantID,restaurantName);
		}
        //Check if restaurant is liked by user
        if(appUser.getlikedRestaurants().contains(restaurant)){
        	throw new Exception ("Restaurant is liked");
		}
        else {
			appUser.addDislikedRestaurants(restaurant);
			restaurant.addDislikedAppUsers(appUser);
			entityManager.merge(appUser);
			entityManager.merge(restaurant);
			return restaurant;
		}
    }

    /**
     * Method to list all the disliked restaurants of a user
     * @return The list of all the disliked restaurants
     */
    public List<String> listAllDisliked(String username) {
        Query q = entityManager.createNativeQuery("SELECT restaurantid FROM disliked_Restaurants WHERE username =:username");
		q.setParameter("username", username);
        @SuppressWarnings("unchecked")
        List<String>dislikedRestaurants = q.getResultList();
        return dislikedRestaurants;
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

    public ResponseEntity<String> getMapping(String url){
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
	 * This is the method to get all the restaurants from a location (ie montreal)
	 * @param location
	 * @return response
	 */
	public ResponseEntity<String> getAllRestaurants(String location) {
		String url = null;
		if (location != null) {
			url = "https://api.yelp.com/v3/businesses/search?location=" + location;
		}
		else {
			return null;
		}
		return getMapping(url);
	}

	/**
	 * This is the method to get a restaurant's info based on its ID
	 * @param id
	 * @return response
	 */
	public ResponseEntity<String> getRestaurant(String id){
		String url = null;
		if (id != null) {
			url = "https://api.yelp.com/v3/businesses/" + id;
		}
		else {
			return null;
		}
		return getMapping(url);
	}

}
