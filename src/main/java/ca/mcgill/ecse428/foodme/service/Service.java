package ca.mcgill.ecse428.foodme.service;
import org.springframework.stereotype.Repository;
import ca.mcgill.ecse428.foodme.model.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;



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
