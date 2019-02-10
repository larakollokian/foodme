package ca.mcgill.ecse428.foodme.service;

import ca.mcgill.ecse428.foodme.model.*;


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
	
	
	
}
