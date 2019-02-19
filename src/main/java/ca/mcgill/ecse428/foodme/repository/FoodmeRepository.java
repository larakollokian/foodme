package ca.mcgill.ecse428.foodme.repository;


import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ca.mcgill.ecse428.foodme.model.*;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
	
}
