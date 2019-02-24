package ca.mcgill.ecse428.foodme.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="restaurant")
public class Restaurant 
{
	//attributes
	private int restaurantID;
	private boolean liked;
	private boolean disliked;
	private String restaurantName;
	
	//associated to
	private AppUser appUser;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	public int getRestaurantID() {
		return restaurantID;
	}

	public void setRestaurantID(int restaurantID) {
		this.restaurantID = restaurantID;
	}

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		if(liked)
		{
			this.disliked = false;
		}
		this.liked = liked;
	}

	public boolean isDisliked() {
		return disliked;
	}

	public void setDisliked(boolean disliked) {
		if(disliked)
		{
			this.liked = false;
		}
		this.disliked = disliked;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	@ManyToOne
	@JsonIgnore
	public AppUser getAppUser() {
		return appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

	@Override
	public String toString() {
		return "Restaurant [restaurantID=" + restaurantID + ", liked=" + liked + ", disliked=" + disliked
				+ ", restaurantName=" + restaurantName + ", appUser=" + appUser + "]";
	}
	
	
	
	
}
