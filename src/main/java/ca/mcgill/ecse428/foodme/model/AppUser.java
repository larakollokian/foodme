package ca.mcgill.ecse428.foodme.model;

import java.util.*;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.*;
import javax.persistence.Table;

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

	//User Association
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

	public boolean setUsername(String aUsername) {
		boolean wasSet = false;
		this.username = aUsername;
		wasSet = true;
		return wasSet;
	}

	public boolean setFirstName(String aFirstName) {
		boolean wasSet = false;
		this.firstName = aFirstName;
		wasSet = true;
		return wasSet;
	}

	public boolean setLastName(String aLastName) {
		boolean wasSet = false;
		this.lastName = aLastName;
		wasSet = true;
		return wasSet;
	}

	public boolean setEmail(String aEmail) {
		boolean wasSet = false;
		this.email = aEmail;
		wasSet = true;
		return wasSet;
	}

	public boolean setPassword(String aPassword) {
		boolean wasSet = false;
		this.password = aPassword;
		wasSet = true;
		return wasSet;
	}

	public boolean setDefaultPreferenceID(int defaultPreferenceID) {
		boolean wasSet = false;
		this.defaultPreferenceID = defaultPreferenceID;
		wasSet = true;
		return wasSet;
	}

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

	public void setlikedRestaurants(Set likedRestaurants) {
		this.likedRestaurants = likedRestaurants;
	}

	public void addlikedRestaurants(Restaurant likedRestaurants){
		if(this.likedRestaurants == null){
			this.likedRestaurants = new HashSet();
		}
		this.likedRestaurants.add(likedRestaurants);
	}
	
	public boolean removelikedRestaurants(Restaurant likedRestaurants) {
		if(this.likedRestaurants.contains(likedRestaurants)) {
			this.likedRestaurants.remove(likedRestaurants);
			return true;
		}
		return false;
	}

	public Set getDislikedRestaurants() {
		return dislikedRestaurants;
	}

	public void setDislikedRestaurants(Set dislikedRestaurants) {
		this.dislikedRestaurants = dislikedRestaurants;
	}

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

	@Override
	public String toString() {
		return "AppUser [username=" + username + ", firstName=" + firstName + ", lastName=" + lastName + ", email="
				+ email + ", password=" + password + ", defaultPreferenceID=" + defaultPreferenceID + "]";
	}

}