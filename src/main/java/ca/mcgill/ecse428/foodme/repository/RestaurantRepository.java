package ca.mcgill.ecse428.foodme.repository;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.mcgill.ecse428.foodme.exception.InvalidInputException;
import ca.mcgill.ecse428.foodme.exception.NullObjectException;
import ca.mcgill.ecse428.foodme.model.AppUser;
import ca.mcgill.ecse428.foodme.model.Restaurant;

@Repository
public class RestaurantRepository {
	
	@PersistenceContext
	private EntityManager entityManager;

	String APIKey = "F5ByVWSif5NWb6w3YYAQjRGOI9Xcg8WKqzBDkPnEl4YDneNpsaKn35YcFEqJyvyV_kUTStuTG2n9-Pi9R7-u9GIkmBQY8LjfNJSrAVEs_K5pGJLCAsWc4N3oxGRgXHYx";

	/**
	 * Method that allows get a user given its username
	 * @param username
	 * @return AppUser
	 * @throws  NullObjectException
	 */
	@Transactional
	public AppUser getAppUser(String username) throws NullObjectException {
		if(entityManager.find(AppUser.class, username) == null) {
			throw new NullObjectException("User does not exist");
		}
		else {
			AppUser appUser = entityManager.find(AppUser.class, username);
			return appUser;
		}
	}

	/**
	 * Method that allows get a restaurant given its restaurantID
	 * @param restaurantID
	 * @return Restaurant
	 * @throws  NullObjectException
	 */
	@Transactional
	public Restaurant getRestaurant(String restaurantID) throws NullObjectException {
		if(entityManager.find(Restaurant.class, restaurantID) == null) {
			throw new NullObjectException("Restaurant does not exist");
		}
		else {
			Restaurant restaurant = entityManager.find(Restaurant.class, restaurantID);
			return restaurant;
		}
	}

	@Transactional
	public Restaurant getRestaurantByName(String restaurantName) throws NullObjectException {
		if(entityManager.find(Restaurant.class, restaurantName) == null) {
			throw new NullObjectException("Restaurant does not exist");
		}
		else {
			Restaurant restaurant = entityManager.find(Restaurant.class, restaurantName);
			return restaurant;
		}
	}

	/**
	 * Method that allows get a restaurant given its restaurantID
	 * @param restaurantID
	 * @return Restaurant
	 * @throws  NullObjectException
	 */
	@Transactional
	public List<Restaurant> getRestaurantQuery(String restaurantID) throws NullObjectException {
		Query q = entityManager.createNativeQuery("SELECT * FROM restaurants WHERE restaurantid =:restaurantID");
		q.setParameter("restaurantID", restaurantID);
		@SuppressWarnings("unchecked")
		List<Restaurant> restaurants = q.getResultList();
		if(restaurants.isEmpty()){
			throw new NullObjectException("No users exist");
		}
		return restaurants;
	}


	/**
	 * Method that gets all restaurants in the database
	 * @return list of Restaurant
	 * @throws  NullObjectException
	 */
	@Transactional
	public List<Restaurant> getAllRestaurants() throws NullObjectException {
		Query q = entityManager.createNativeQuery("SELECT * FROM restaurants");
		@SuppressWarnings("unchecked")
		List<Restaurant> restaurants = q.getResultList();
		if(restaurants.isEmpty()){
			throw new NullObjectException("No restaurants exists");
		}
		return restaurants;
	}


	/**
	 * Method that creates a new restaurant
	 * @param restaurantID
	 * @param restaurantName
	 * @return Restaurant
	 * @throws InvalidInputException
	 * @throws IllegalArgumentException
	 */
	public Restaurant createRestaurant(String restaurantID, String restaurantName) throws InvalidInputException,IllegalArgumentException{
		if(restaurantID.length() == 0 || restaurantName.length() == 0){
			throw new InvalidInputException("restaurantID and restaurantName must be at least 1 character");
		}
		if(entityManager.find(Restaurant.class,restaurantID) != null){
			throw new IllegalArgumentException("Restaurant already exists");
		}
		Restaurant restaurant = new Restaurant();
		restaurant.setRestaurantID(restaurantID);
		restaurant.setRestaurantName(restaurantName);
		entityManager.persist(restaurant);
		return restaurant;
	}


	public Restaurant deleteRestaurant(String restaurantName) throws InvalidInputException {
		
		if(restaurantName.length() == 0){
			throw new InvalidInputException("restaurantID and restaurantName must be at least 1 character");
		}
		
		else if (entityManager.find(Restaurant.class, restaurantName) == null) {
			throw new InvalidInputException("Restaurant does not exists");
		}
		else {
			Restaurant restaurant =  entityManager.find(Restaurant.class, restaurantName);
			entityManager.remove(restaurant);
			return restaurant;

		}
		
	}

	/**
	 * Method that adds a restaurant and a user to the likedRestaurant list in the database
	 * @param username
	 * @param restaurantID
	 * @param restaurantName
	 * @return Restaurant
	 * @throws IllegalArgumentException
	 * @throws NullObjectException
	 * @throws InvalidInputException
	 */
	@Transactional
	public Restaurant addLiked(String username, String restaurantID, String restaurantName) throws  Exception {
		AppUser appUser = getAppUser(username);
		Restaurant restaurant = new Restaurant();

		try {
			restaurant = getRestaurant(restaurantID);
		}
		catch(NullObjectException e1){
			restaurant = createRestaurant(restaurantID,restaurantName);
		}

		//Check if restaurant is disliked by user
		if(appUser.getDislikedRestaurants().contains(restaurant)){
			throw new IllegalArgumentException ("Restaurant is disliked by user!!!");
		}

		//Check if restaurant is liked by user
		if(appUser.getlikedRestaurants().contains(restaurant)){
			throw new IllegalArgumentException ("Restaurant is already liked by user!!!");
		}

		appUser.addlikedRestaurants(restaurant);
		restaurant.addLikedAppUsers(appUser);
		entityManager.merge(appUser);
		entityManager.merge(restaurant);
		return restaurant;
	}

	/**
	 * Method that remove a restaurant and a user of the likedRestaurant list in the database
	 * @param username
	 * @param restaurantID
	 * @return Restaurant
	 * @throws IllegalArgumentException
	 * @throws NullObjectException
	 * @throws InvalidInputException
	 */
	@Transactional
	public Restaurant removeLiked(String username, String restaurantID) throws  Exception {
		AppUser appUser = getAppUser(username);
		Restaurant restaurant = new Restaurant();

		try {
			restaurant = getRestaurant(restaurantID);
		}
		catch(NullObjectException e1){
			throw new NullObjectException("Restaurant doesn't exist");
		}

		//Check if restaurant is liked by user
		if(appUser.getlikedRestaurants().contains(restaurant) && restaurant.getAppUser_likes().contains(appUser)){
			appUser.removelikedRestaurants(restaurant);
			restaurant.removeLikedAppUsers(appUser);
			entityManager.merge(appUser);
			entityManager.merge(restaurant);
			return restaurant;
		}
		else{
			throw new IllegalArgumentException ("Restaurant is not liked by user!!!");
		}
	}

	/**
	 * Method that lists all the liked restaurants of a user
	 * @return list of liked restaurants
	 * @throws NullObjectException
	 */
	public List<String> listAllLiked(String username) throws NullObjectException {
		Query q = entityManager.createNativeQuery("SELECT restaurantid FROM liked_Restaurants WHERE username =:username");
		q.setParameter("username", username);
		@SuppressWarnings("unchecked")
		List<String>likedRestaurants = q.getResultList();
		if (likedRestaurants.size() == 0){
			throw new NullObjectException ("User does not have liked restaurants");
		}
		return likedRestaurants;
	}

	/**
	 * Method that adds a restaurant and a user to the dislikedRestaurant list in the database
	 * @param username
	 * @param restaurantID
	 * @param restaurantName
	 * @return Restaurant
	 * @throws IllegalArgumentException
	 * @throws NullObjectException
	 */
	@Transactional
	public Restaurant addDisliked(String username, String restaurantID, String restaurantName) throws Exception {
		AppUser appUser = getAppUser(username);
		Restaurant restaurant = new Restaurant();

		try {
			restaurant = getRestaurant(restaurantID);
		}
		catch(NullObjectException e1){
			restaurant = createRestaurant(restaurantID,restaurantName);
		}

		//Check if restaurant is liked by user
		if(appUser.getlikedRestaurants().contains(restaurant)){
			throw new IllegalArgumentException ("Restaurant is liked by user!!!");
		}

		//Check if restaurant is disliked by user
		if(appUser.getDislikedRestaurants().contains(restaurant)){
			throw new IllegalArgumentException ("Restaurant is already disliked by user!!!");
		}

		appUser.addDislikedRestaurants(restaurant);
		restaurant.addDislikedAppUsers(appUser);
		entityManager.merge(appUser);
		entityManager.merge(restaurant);
		return restaurant;
	}
	
	@Transactional
    public Restaurant removeDisliked(String username, String restaurantID) throws NullObjectException, InvalidInputException  {
		AppUser appUser = getAppUser(username);
		Restaurant restaurant = new Restaurant();
		
		restaurant = getRestaurant(restaurantID);
		
		if(!appUser.getDislikedRestaurants().contains(restaurant)){
			throw new InvalidInputException ("Restaurant is not on disliked list!!!");
		}

		appUser.removeDislikedRestaurants(restaurant);
		restaurant.removeDislikedAppUsers(appUser);
		entityManager.merge(appUser);
		entityManager.merge(restaurant);
		return restaurant;
	}

    /**
     * Method to list all the disliked restaurants of a user
     * @return The list of all the disliked restaurants
     */
    public List<String> listAllDisliked(String username)throws NullObjectException {
        Query q = entityManager.createNativeQuery("SELECT restaurantid FROM disliked_Restaurants WHERE username =:username");
		q.setParameter("username", username);
        @SuppressWarnings("unchecked")
        List<String> dislikedRestaurants = q.getResultList();
		if (dislikedRestaurants.size() == 0){
			throw new NullObjectException ("User does not have liked restaurants");
		}
        return dislikedRestaurants;
    }

	/**
	 * Method that adds a restaurant and a user to the visitedRestaurant list in the database
	 * @param username
	 * @param restaurantID
	 * @param restaurantName
	 * @return Restaurant
	 * @throws IllegalArgumentException
	 * @throws NullObjectException
	 */
	@Transactional
	public Restaurant addVisited(String username, String restaurantID, String restaurantName) throws Exception {

		AppUser appUser = getAppUser(username);
		Restaurant restaurant = new Restaurant();

		try {
			restaurant = getRestaurant(restaurantID);
		}
		catch(NullObjectException e1){
			restaurant = createRestaurant(restaurantID,restaurantName);
		}

		//Remove it to put it at top of the list
		if(appUser.getVisitedRestaurants().contains(restaurant)){
			//Do nothing already in list
			return restaurant;
		}

		appUser.addVisitedRestaurants(restaurant);
		restaurant.addVisitedAppUsers(appUser);
		entityManager.merge(appUser);
		entityManager.merge(restaurant);
		return restaurant;
	}

	/**
	 * Method that clears the
	 * @param username
	 * @return Restaurant
	 * @throws IllegalArgumentException
	 * @throws NullObjectException
	 */
	@Transactional
	public Boolean clearVisited(String username) throws NullObjectException {
		AppUser appUser = getAppUser(username);
		if(appUser.getVisitedRestaurants().size()>0){
			for(Restaurant r : (Set<Restaurant>)appUser.getVisitedRestaurants()){
				r.removeVisitedAppUsers(appUser);
				entityManager.merge(r);
			}
			appUser.getVisitedRestaurants().clear();
			entityManager.merge(appUser);
			return true;
		}
		else{
			throw new NullObjectException ("Visited list is empty");
		}
	}


	/**
	 * Method to list all the visited restaurants of a user
	 * @return The list of all the visited restaurants
	 */
	public List<String> listAllVisited(String username)throws NullObjectException {
		Query q = entityManager.createNativeQuery("SELECT restaurantid FROM visited_Restaurants WHERE username =:username");
		q.setParameter("username", username);
		@SuppressWarnings("unchecked")
		List<String> visitedRestaurants = q.getResultList();
		if (visitedRestaurants.size() == 0){
			throw new NullObjectException ("User hasn't visited a restaurant");
		}
		return visitedRestaurants;
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


}

