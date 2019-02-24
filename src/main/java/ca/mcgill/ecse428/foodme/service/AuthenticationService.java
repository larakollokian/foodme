package ca.mcgill.ecse428.foodme.service;

import ca.mcgill.ecse428.foodme.model.*;
import ca.mcgill.ecse428.foodme.repository.FoodmeRepository;
import ca.mcgill.ecse428.foodme.security.*;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class AuthenticationService {
	
	@Autowired
	private FoodmeRepository repository;

	private static final HashMap<String, String> userBySession = new HashMap<>();
	private static final HashMap<String, String> sessionByUser = new HashMap<>();

	/**
	 * Constructor
	 */
	public AuthenticationService() {}
	
	/**
	 * gets an AppUser by session 
	 * @param sessionGuid
	 * @return AppUser 
	 * @throws Exception 
	 */
	public AppUser getUserBySession(String sessionGuid) throws InvalidSessionException {
		String name = userBySession.get(sessionGuid);
		if (name == null) {
			throw new InvalidSessionException("Session has expired or is invalid.");
		}
		AppUser user = findUserByUsername(name);
		return user;
	}
	/**
	 * gets an AppUser in the repository using the username
	 * @param username
	 * @return AppUser
	 * @throws Exception 
	 */
	private AppUser findUserByUsername(String username) throws InvalidSessionException  {

		AppUser user = repository.getAppUser(username);
		if (user == null){
			//User no longer/do not exist
			throw new InvalidSessionException("User does not exist");
		}
		return user;
	}
	/**
	 * allows to login: validate username and password
	 * @param username
	 * @param password
	 * @return sessionGuid
	 * @throws AuthenticationException 

	 */
	public String login(String username, String password) throws Exception{
		AppUser user = null;
		try{
			user = findUserByUsername(username);
			Password.check(password, user.getPassword());
		}
		catch(InvalidSessionException e){
			throw new InvalidSessionException("User does not exist");
		}
		catch(AuthenticationException e){
			throw new AuthenticationException("Invalid login password!!!",e);
		}
		catch(Exception e){
			e.printStackTrace();
		}
			
		if (sessionByUser.containsKey(user.getUsername())) {
			// Invalidate old session
			logout(user.getUsername());
		}
		String sessionGuid = UUID.randomUUID().toString();
		userBySession.put(sessionGuid, user.getUsername());
		sessionByUser.put(user.getUsername(), sessionGuid);
		return sessionGuid;
		
		
	}
	/**
	 * allows to logout having the username
	 * @param username
	 */
	public void logout(String username) {
		userBySession.remove(sessionByUser.remove(username));

	}
}