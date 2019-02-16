package ca.mcgill.ecse428.foodme.service.Authentication;

import ca.mcgill.ecse428.foodme.model.*;
import java.util.HashMap;
//import java.util.UUID;

public class AuthenticationService {
	
	
	public AuthenticationService() {
	}
	
	private static final HashMap<String, Integer> userBySession = new HashMap<>();
	private static final HashMap<Integer, String> sessionByUser = new HashMap<>();

	public User getUserBySession(String sessionGuid) throws InvalidSessionException {
        return null;

	}
	
	private User findUserById(int id) {
		return null;
	}
	
	public String login(String username, String password) throws AuthenticationException {
		return null;
	}
	
	public void logout(String sessionGuid) {
		sessionByUser.remove(userBySession.remove(sessionGuid));
	}
	
	public void logout(int userId) {
		userBySession.remove(sessionByUser.remove(userId));
		
	}
}