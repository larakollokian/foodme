/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4408.bc0cac3a4 modeling language!*/

package ca.mcgill.ecse428.foodme.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name="preference")
public class Preference
{
	//------------------------
	// MEMBER VARIABLES
	//------------------------

	//Preference Attributes
	private DistanceRange distance;
	private Cuisine cuisine;
	private PriceRange price;
	private Rating rating;

	private int pID;

	//Preference Associations
	private AppUser appUser;

	//------------------------
	// CONSTRUCTOR (SHOULD BE DEFAULT)
	//------------------------

	//  public Preference(DistanceRange aDistance, Cuisine aCuisine, PriceRange aPrice, Rating aRating, String aPID, User aUser)
	//  {
	//    distance = aDistance;
	//    cuisine = aCuisine;
	//    price = aPrice;
	//    rating = aRating;
	//    pID = aPID;
	//    boolean didAddUser = setUser(aUser);
	//    if (!didAddUser)
	//    {
	//      throw new RuntimeException("Unable to create preference due to user");
	//    }
	//  }

	//------------------------
	// INTERFACE
	//------------------------

	public boolean setDistance(DistanceRange aDistance)
	{
		boolean wasSet = false;
		this.distance = aDistance;
		wasSet = true;
		return wasSet;
	}

	public boolean setCuisine(Cuisine aCuisine)
	{
		boolean wasSet = false;
		this.cuisine = aCuisine;
		wasSet = true;
		return wasSet;
	}

	public boolean setPrice(PriceRange aPrice)
	{
		boolean wasSet = false;
		this.price = aPrice;
		wasSet = true;
		return wasSet;
	}

	public boolean setRating(Rating aRating)
	{
		boolean wasSet = false;
		this.rating = aRating;
		wasSet = true;
		return wasSet;
	}

	public boolean setPID(int aPID)
	{
		boolean wasSet = false;
		this.pID = aPID;
		wasSet = true;
		return wasSet;
	}

	public DistanceRange getDistance()
	{
		if(this.distance == null)
		{
			this.distance = DistanceRange.None;
		}
		return this.distance;
	}

	public Cuisine getCuisine()
	{
		if(this.cuisine == null)
		{
			this.cuisine = Cuisine.None;
		}
		return this.cuisine;
	}

	public PriceRange getPrice()
	{
		if(this.price == null)
		{
			this.price = PriceRange.None;
		}
		return this.price;
	}

	public Rating getRating()
	{
		if(this.rating == null)
		{
			this.rating = Rating.None;
		}
		return this.rating;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)

	public int getPID()
	{
		return this.pID;
	}

g
	@ManyToOne
	//@JoinColumn(name = "app_user")
	@JsonIgnore
	public AppUser getUser()
	{
		return this.appUser;
	}

	public void setUser(AppUser aUser)
	{
		this.appUser = aUser;
	}

	public void delete()
	{
		AppUser placeholderUser = appUser;
		this.appUser = null;
		if(placeholderUser != null)
		{
			placeholderUser.removePreference(this);
		}
	}


	public String toString()
	{
		return super.toString() + "["+
				"pID" + ":" + getPID()+ "]" + System.getProperties().getProperty("line.separator") +
				"  " + "distance" + "=" + (getDistance() != null ? !getDistance().equals(this)  ? getDistance().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
				"  " + "cuisine" + "=" + (getCuisine() != null ? !getCuisine().equals(this)  ? getCuisine().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
				"  " + "price" + "=" + (getPrice() != null ? !getPrice().equals(this)  ? getPrice().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
				"  " + "rating" + "=" + (getRating() != null ? !getRating().equals(this)  ? getRating().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
				"  " + "user = "+(getUser()!=null?Integer.toHexString(System.identityHashCode(getUser())):"null");
	}
}