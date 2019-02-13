package ca.mcgill.ecse428.foodme.service;

import org.springframework.stereotype.Repository;

import ca.mcgill.ecse428.foodme.model.*;

@Repository
public class Service {

	public void changePassword(User aUser, String newPassword) {
		aUser.setPassword(newPassword);
		return;
		
	}
	
	public void deleteAccount(User aUser) {
		aUser.setCity(null);
		aUser.setEmail(null);
		aUser.setName(null);
		aUser.setPassword(null);
	}
	
	public boolean isRestaurantOpen(Restaurant aRestaurant) {
	
		
		return true;
	}
}
