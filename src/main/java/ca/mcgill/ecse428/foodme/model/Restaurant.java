package ca.mcgill.ecse428.foodme.model;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="Restaurants")
public class Restaurant {
	//Attributes
	@Id private String restaurantID;

	private String restaurantName;
	
	//Associations
	@ManyToMany(mappedBy = "likedRestaurants")
	private Set<AppUser> appUser_likes;

	@ManyToMany(mappedBy = "dislikedRestaurants")
	private Set<AppUser> appUser_dislikes;

	@ManyToMany(mappedBy = "visitedRestaurants")
	private Set<AppUser> appUser_visited;

	//Setters
	public void setRestaurantID(String restaurantID) {
		this.restaurantID = restaurantID;
	}
	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}
	public void setAppUser_likes(Set appUser_likes) {
		this.appUser_likes = appUser_likes;
	}
	public void setAppUser_dislikes(Set appUser_dislikes) {
		this.appUser_dislikes = appUser_dislikes;
	}
	public void setAppUser_visited(Set appUser_visited) {
		this.appUser_visited = appUser_visited;
	}

	//Getters
	public String getRestaurantID() {
		return restaurantID;
	}
	public String getRestaurantName() {
		return restaurantName;
	}
	public Set getAppUser_likes() {
		return appUser_likes;
	}
	public Set getAppUser_dislikes() {
		return appUser_dislikes;
	}
	public Set getAppUser_visited() {
		return appUser_visited;
	}

	//Liked list
	public void addLikedAppUsers(AppUser user){
		if(this.appUser_likes == null){
			this.appUser_likes = new HashSet();
		}
		this.appUser_likes.add(user);
	}

	public boolean removeLikedAppUsers(AppUser user) {
		if(this.appUser_likes.contains(user)) {
			this.appUser_likes.remove(user);
			return true;
		}
		return false;
	}

	//Disliked list
	public void addDislikedAppUsers(AppUser user){
		if(this.appUser_dislikes == null){
			this.appUser_dislikes = new HashSet();
		}
		this.appUser_dislikes.add(user);
	}

	public boolean removeDislikedAppUsers(AppUser user) {
		if(this.appUser_dislikes.contains(user)) {
			this.appUser_dislikes.remove(user);
			return true;
		}
		return false;
	}

	//Visited list
	public void addVisitedAppUsers(AppUser user){
		if(this.appUser_visited == null){
			this.appUser_visited = new HashSet();
		}
		this.appUser_visited.add(user);
	}

	public boolean removeVisitedAppUsers(AppUser user) {
		if(this.appUser_visited.contains(user)) {
			this.appUser_visited.remove(user);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Restaurant [restaurantID=" + restaurantID + ", liked="
				+ ", restaurantName=" + restaurantName + "]";
	}
	
	
	
	
}
