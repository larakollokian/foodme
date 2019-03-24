package ca.mcgill.ecse428.foodme.model;

import java.util.*;
import javax.persistence.*;

@Entity
@Table(name="AppUsers")
public class AppUser {

	//User Attributes
	@Id private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private int defaultPreferenceID;

	//User Associations
	@ManyToMany
	@JoinTable(name = "likedRestaurants",
			joinColumns = @JoinColumn(name = "username"),
			inverseJoinColumns = @JoinColumn(name = "restaurantID"))
	private Set<Restaurant> likedRestaurants;

	@ManyToMany
	@JoinTable(
			name = "dislikedRestaurants",
			joinColumns = @JoinColumn(name = "username"),
			inverseJoinColumns = @JoinColumn(name = "restaurantID"))
	private Set<Restaurant> dislikedRestaurants;

	@ManyToMany
	@JoinTable(
			name = "visitedRestaurants",
			joinColumns = @JoinColumn(name = "username"),
			inverseJoinColumns = @JoinColumn(name = "restaurantID"))
	private Set<Restaurant> visitedRestaurants;

	//Setters
	public void setUsername(String username) { this.username = username; }
	public void setFirstName(String firstName) { this.firstName = firstName; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	public void setEmail(String email) { this.email = email; }
	public void setPassword(String password) { this.password = password; }
	public void setDefaultPreferenceID(int defaultPreferenceID) { this.defaultPreferenceID = defaultPreferenceID; }
	public void setLikedRestaurants(Set likedRestaurants) {
		this.likedRestaurants = likedRestaurants;
	}
	public void setDislikedRestaurants(Set dislikedRestaurants) {
		this.dislikedRestaurants = dislikedRestaurants;
	}
	public void setVisitedRestaurants(Set visitedRestaurants) {
		this.visitedRestaurants = visitedRestaurants;
	}

	//Getters
	public String getUsername()
	{
		return this.username;
	}
	public String getFirstName()
	{
		return this.firstName;
	}
	public String getLastName()
	{
		return this.lastName;
	}
	public String getEmail()
	{
		return this.email;
	}
	public String getPassword()
	{
		return this.password;
	}
	public int getDefaultPreferenceID() { return this.defaultPreferenceID; }
	public Set getlikedRestaurants() {
		return likedRestaurants;
	}
	public Set getDislikedRestaurants() {
		return dislikedRestaurants;
	}
	public Set getVisitedRestaurants() { return visitedRestaurants; }

	//Liked list
	public void addLikedRestaurants(Restaurant likedRestaurants){
		if(this.likedRestaurants == null){
			this.likedRestaurants = new HashSet();
		}
		this.likedRestaurants.add(likedRestaurants);
	}
	
	public boolean removeLikedRestaurants(Restaurant likedRestaurants) {
		if(this.likedRestaurants.contains(likedRestaurants)) {
			this.likedRestaurants.remove(likedRestaurants);
			return true;
		}
		return false;
	}

	//Disliked list
	public void addDislikedRestaurants(Restaurant dislikedRestaurants){
		if(this.dislikedRestaurants == null){
			this.dislikedRestaurants = new HashSet();
		}
		this.dislikedRestaurants.add(dislikedRestaurants);
	}

	public boolean removeDislikedRestaurants(Restaurant dislikedRestaurants) {
		if(this.dislikedRestaurants.contains(dislikedRestaurants)) {
			this.dislikedRestaurants.remove(dislikedRestaurants);
			return true;
		}
		return false;
	}

	//Visited list
	public void addVisitedRestaurants(Restaurant visitedRestaurants){
		if(this.visitedRestaurants == null){
			this.visitedRestaurants = new HashSet();
		}
		this.visitedRestaurants.add(visitedRestaurants);
	}

	public boolean removedVisitedRestaurants(Restaurant visitedRestaurants) {
		if(this.visitedRestaurants.contains(visitedRestaurants)) {
			this.visitedRestaurants.remove(visitedRestaurants);
			return true;
		}
		return false;
	}

}