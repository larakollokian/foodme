package ca.mcgill.ecse428.foodme.repository;


import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ca.mcgill.ecse428.foodme.model.*;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

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
		// TODO return exception if enum don't exist, probably will never happen if we have dropdown menus though
		preference.setPrice(PriceRange.valueOf(priceRange));
		preference.setDistance(DistanceRange.valueOf(distanceRange));
		preference.setCuisine(Cuisine.valueOf(cuisine));
		preference.setRating(Rating.valueOf(rating));
		preference.setUser(user);
		entityManager.persist(preference);
		return preference;
	}

	@Transactional
	public void addPreferenceToUser(AppUser user, Preference preference) {
		user.addPreference(preference);
		entityManager.persist(user);
	}

	@Transactional
	public Preference editPreference(Preference editPreference, PriceRange priceRange, DistanceRange distanceRange, Cuisine cuisine, Rating rating) {
		editPreference.setPrice(priceRange);
		editPreference.setDistance(distanceRange);
		editPreference.setCuisine(cuisine);
		editPreference.setRating(rating);
		entityManager.persist(editPreference);
		return editPreference;
	}

	@Transactional
	public void updatePreferenceToUser(AppUser user, int index, Preference editPreference) {
		user.getPreferences().set(index, editPreference);
		entityManager.persist(user);
	}

	@Transactional
	public AppUser getAppUser(String username){
		AppUser appUser = entityManager.find(AppUser.class, username);
		return appUser;
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
