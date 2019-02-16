/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4408.bc0cac3a4 modeling language!*/

package ca.mcgill.ecse428.foodme.model;
import java.util.*;

// line 4 "../../../../../../../ump/tmp570555/model.ump"
// line 40 "../../../../../../../ump/tmp570555/model.ump"
public class User
{
//------------------------
	// MEMBER VARIABLES
	//------------------------

	//User Attributes
	private String email;
	private String password;
	private String name;
	private String city;

	//User Associations
	private List<Preference> preferences;
	private List<Restaurant> restaurants;

	private List<Restaurant> liked;
	private List<Restaurant> disliked;

	//------------------------
	// CONSTRUCTOR
	//------------------------

	public User(String aEmail, String aPassword, String aName, String aCity)
	{
		email = aEmail;
		password = aPassword;
		name = aName;
		city = aCity;
		preferences = new ArrayList<Preference>();
		restaurants = new ArrayList<Restaurant>();
	}

	//------------------------
	// INTERFACE
	//------------------------

	public boolean setEmail(String aEmail)
	{
		boolean wasSet = false;
		email = aEmail;
		wasSet = true;
		return wasSet;
	}

	public boolean setPassword(String aPassword)
	{
		boolean wasSet = false;
		password = aPassword;
		wasSet = true;
		return wasSet;
	}

	public boolean setName(String aName)
	{
		boolean wasSet = false;
		name = aName;
		wasSet = true;
		return wasSet;
	}

	public boolean setCity(String aCity)
	{
		boolean wasSet = false;
		city = aCity;
		wasSet = true;
		return wasSet;
	}

	public String getEmail()
	{
		return email;
	}

	public String getPassword()
	{
		return password;
	}

	public String getName()
	{
		return name;
	}

	public String getCity()
	{
		return city;
	}
	/* Code from template association_GetMany */
	public Preference getPreference(int index)
	{
		Preference aPreference = preferences.get(index);
		return aPreference;
	}

	public List<Preference> getPreferences()
	{
		List<Preference> newPreferences = Collections.unmodifiableList(preferences);
		return newPreferences;
	}

	public int numberOfPreferences()
	{
		int number = preferences.size();
		return number;
	}

	public boolean hasPreferences()
	{
		boolean has = preferences.size() > 0;
		return has;
	}

	public int indexOfPreference(Preference aPreference)
	{
		int index = preferences.indexOf(aPreference);
		return index;
	}
	/* Code from template association_GetMany */
	public Restaurant getRestaurant(int index)
	{
		Restaurant aRestaurant = restaurants.get(index);
		return aRestaurant;
	}

	public List<Restaurant> getRestaurants()
	{
		List<Restaurant> newRestaurants = Collections.unmodifiableList(restaurants);
		return newRestaurants;
	}

	public int numberOfRestaurants()
	{
		int number = restaurants.size();
		return number;
	}

	public boolean hasRestaurants()
	{
		boolean has = restaurants.size() > 0;
		return has;
	}

	public int indexOfRestaurant(Restaurant aRestaurant)
	{
		int index = restaurants.indexOf(aRestaurant);
		return index;
	}
	/* Code from template association_MinimumNumberOfMethod */
	public static int minimumNumberOfPreferences()
	{
		return 0;
	}
	/* Code from template association_AddManyToOne */
	public Preference addPreference(String aRange, String aDistance, String aCuisine, boolean aOpen, int aRating)
	{
		return new Preference(aRange, aDistance, aCuisine, aOpen, aRating, this);
	}

	public boolean addPreference(Preference aPreference)
	{
		boolean wasAdded = false;
		if (preferences.contains(aPreference)) { return false; }
		User existingUser = aPreference.getUser();
		boolean isNewUser = existingUser != null && !this.equals(existingUser);
		if (isNewUser)
		{
			aPreference.setUser(this);
		}
		else
		{
			preferences.add(aPreference);
		}
		wasAdded = true;
		return wasAdded;
	}

	public boolean removePreference(Preference aPreference)
	{
		boolean wasRemoved = false;
		//Unable to remove aPreference, as it must always have a user
		if (!this.equals(aPreference.getUser()))
		{
			preferences.remove(aPreference);
			wasRemoved = true;
		}
		return wasRemoved;
	}
	/* Code from template association_AddIndexControlFunctions */
	public boolean addPreferenceAt(Preference aPreference, int index)
	{  
		boolean wasAdded = false;
		if(addPreference(aPreference))
		{
			if(index < 0 ) { index = 0; }
			if(index > numberOfPreferences()) { index = numberOfPreferences() - 1; }
			preferences.remove(aPreference);
			preferences.add(index, aPreference);
			wasAdded = true;
		}
		return wasAdded;
	}

	public boolean addOrMovePreferenceAt(Preference aPreference, int index)
	{
		boolean wasAdded = false;
		if(preferences.contains(aPreference))
		{
			if(index < 0 ) { index = 0; }
			if(index > numberOfPreferences()) { index = numberOfPreferences() - 1; }
			preferences.remove(aPreference);
			preferences.add(index, aPreference);
			wasAdded = true;
		} 
		else 
		{
			wasAdded = addPreferenceAt(aPreference, index);
		}
		return wasAdded;
	}
	/* Code from template association_MinimumNumberOfMethod */
	public static int minimumNumberOfRestaurants()
	{
		return 0;
	}
	/* Code from template association_AddManyToOne */
	public Restaurant addRestaurant(String aId, String aPrice, String aTypeCuisine, String aTerm, String aLocation, double aLatitude, double aLongitude, int aRadius, String aCategories, int aLimit, String aSort_by, boolean aIsOpen, int aOpenAt, int aRating)
	{
		return new Restaurant(aId, aPrice, aTypeCuisine, aTerm, aLocation, aLatitude, aLongitude, aRadius, aCategories, aLimit, aSort_by, aIsOpen, aOpenAt, aRating, this);
	}

	public boolean addRestaurant(Restaurant aRestaurant)
	{
		boolean wasAdded = false;
		if (restaurants.contains(aRestaurant)) { return false; }
		User existingUser = aRestaurant.getUser();
		boolean isNewUser = existingUser != null && !this.equals(existingUser);
		if (isNewUser)
		{
			aRestaurant.setUser(this);
		}
		else
		{
			restaurants.add(aRestaurant);
		}
		wasAdded = true;
		return wasAdded;
	}

	public boolean removeRestaurant(Restaurant aRestaurant)
	{
		boolean wasRemoved = false;
		//Unable to remove aRestaurant, as it must always have a user
		if (!this.equals(aRestaurant.getUser()))
		{
			restaurants.remove(aRestaurant);
			wasRemoved = true;
		}
		return wasRemoved;
	}
	/* Code from template association_AddIndexControlFunctions */
	public boolean addRestaurantAt(Restaurant aRestaurant, int index)
	{  
		boolean wasAdded = false;
		if(addRestaurant(aRestaurant))
		{
			if(index < 0 ) { index = 0; }
			if(index > numberOfRestaurants()) { index = numberOfRestaurants() - 1; }
			restaurants.remove(aRestaurant);
			restaurants.add(index, aRestaurant);
			wasAdded = true;
		}
		return wasAdded;
	}

	public boolean addOrMoveRestaurantAt(Restaurant aRestaurant, int index)
	{
		boolean wasAdded = false;
		if(restaurants.contains(aRestaurant))
		{
			if(index < 0 ) { index = 0; }
			if(index > numberOfRestaurants()) { index = numberOfRestaurants() - 1; }
			restaurants.remove(aRestaurant);
			restaurants.add(index, aRestaurant);
			wasAdded = true;
		} 
		else 
		{
			wasAdded = addRestaurantAt(aRestaurant, index);
		}
		return wasAdded;
	}

	public void delete()
	{
		for(int i=preferences.size(); i > 0; i--)
		{
			Preference aPreference = preferences.get(i - 1);
			aPreference.delete();
		}
		for(int i=restaurants.size(); i > 0; i--)
		{
			Restaurant aRestaurant = restaurants.get(i - 1);
			aRestaurant.delete();
		}
	}


	public String toString()
	{
		return super.toString() + "["+
				"email" + ":" + getEmail()+ "," +
				"password" + ":" + getPassword()+ "," +
				"name" + ":" + getName()+ "," +
				"city" + ":" + getCity()+ "]";
	}

	public List<Restaurant> getLiked() {
		return liked;
	}

	public void setLiked(List<Restaurant> liked) {
		this.liked = liked;
	}

	public List<Restaurant> getDisliked() {
		return disliked;
	}

	public void setDisliked(List<Restaurant> disliked) {
		this.disliked = disliked;
	}

}