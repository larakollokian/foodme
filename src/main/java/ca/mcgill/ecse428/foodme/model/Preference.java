package ca.mcgill.ecse428.foodme.model;

import ca.mcgill.ecse428.foodme.model.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name="Preferences")
public class Preference {

	//Primary key
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int pID;

	//Attributes
	private String location;
	private String cuisine;
	private String price;	//$,$$,$$$,$$$$,$$$$$
	private String sortBy;	//best_match, rating, review_count, distance

	//Associations
	@ManyToOne
	private AppUser appUser;

	public boolean setPID(int aPID) {
		boolean wasSet = false;
		this.pID = aPID;
		wasSet = true;
		return wasSet;
	}

	public boolean setLocation(String aLocation) {
		boolean wasSet = false;
		this.location = aLocation;
		wasSet = true;
		return wasSet;
	}

	public boolean setCuisine(String aCuisine) {
		boolean wasSet = false;
		this.cuisine = aCuisine;
		wasSet = true;
		return wasSet;
	}

	public boolean setPrice(String aPrice)
	{
		boolean wasSet = false;
		this.price = aPrice;
		wasSet = true;
		return wasSet;
	}

	public boolean setSortBy(String sortBy) {
		boolean wasSet = false;
		this.sortBy = sortBy;
		wasSet = true;
		return wasSet;
	}


	public int getPID() {
		return this.pID;
	}

	public String getLocation() {
		return this.location;
	}

	public String getCuisine() {
		return this.cuisine;
	}

	public String getPrice() {
		return this.price;
	}

	public String getSortBy() {
		return this.sortBy;
	}

	public AppUser getUser() {
		return this.appUser;
	}

	public void setUser(AppUser aUser) {
		this.appUser = aUser;
	}

	public void deleteUser() {
		this.appUser = null;
	}

	public String toString()
	{
		return super.toString() + "["+
				"pID" + ":" + getPID()+ "]" + System.getProperties().getProperty("line.separator") +
				"  " + "location" + "=" + (getLocation() != null ? !getLocation().equals(this)  ? getLocation().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
				"  " + "cuisine" + "=" + (getCuisine() != null ? !getCuisine().equals(this)  ? getCuisine().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
				"  " + "price" + "=" + (getPrice() != null ? !getPrice().equals(this)  ? getPrice().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
				"  " + "sortBy" + "=" + (getSortBy() != null ? !getSortBy().equals(this)  ? getSortBy().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
				"  " + "user = "+(getUser()!=null?Integer.toHexString(System.identityHashCode(getUser())):"null");
	}
}