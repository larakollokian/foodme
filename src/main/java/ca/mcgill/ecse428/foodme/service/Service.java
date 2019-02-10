package ca.mcgill.ecse428.foodme.service;


import java.util.List;

import ca.mcgill.ecse428.foodme.model.Restaurant;
import ca.mcgill.ecse428.foodme.model.User;

public class Service {

	public Restaurant restaurant;
	public User user;
	private List<Restaurant> liked;
	
	/**
	 * Method to like a restaurant so its in the user list of liked restaurant
	 * @param restaurant The restaurant a user likes
	 * @return void The method returns nothing, this change will be saved in the database
	 */
	public void isLiked(Restaurant restaurant) {
		liked=user.getLiked();
		liked.add(restaurant);
		user.setLiked(liked);
	}
	
	/**
	 * Method to list all the liked restaurants of a user
	 * @return The list of all the liked restaurants
	 */
	public List<Restaurant> listAllLiked() {
		List<Restaurant> liked = user.getLiked();
		return liked;
	}
	
}
