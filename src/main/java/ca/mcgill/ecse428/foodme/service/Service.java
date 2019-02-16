package ca.mcgill.ecse428.foodme.service;
import org.springframework.stereotype.Repository;
import ca.mcgill.ecse428.foodme.model.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;



@Repository
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

	/**
	 * Method that allows user to update their password
	 * 
	 * @param aUser
	 * @param newPassword
	 */

	public void changePassword(User aUser, String newPassword) {
		aUser.setPassword(newPassword);
		return;
		
	}

	/**
	 * Method that allows users to delete their existing account.
	 * @param aUser
	 */
	public void deleteAccount(User aUser) {
		aUser.setCity(null);
		aUser.setEmail(null);
		aUser.setName(null);
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

