/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4426.31aab51de modeling language!*/



// line 2 "model.ump"
// line 47 "model.ump"
// line 58 "model.ump"
public class Preference
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum Cuisine { Mexican, Italian }
  public enum PriceRange { $, $$, $$$, $$$$, $$$$$ }
  public enum DistanceRange { 250m, 500m, 750m, 1km, 2km+ }
  public enum Rating { 0, 05, 1, 15, 2, 25, 3, 35, 4, 45, 5 }

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Preference Attributes
  private DistanceRange distance;
  private Cuisine cuisine;
  private PriceRange price;
  private Rating rating;
  private String pID;

  //Preference Associations
  private User user;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Preference(DistanceRange aDistance, Cuisine aCuisine, PriceRange aPrice, Rating aRating, String aPID, User aUser)
  {
    distance = aDistance;
    cuisine = aCuisine;
    price = aPrice;
    rating = aRating;
    pID = aPID;
    boolean didAddUser = setUser(aUser);
    if (!didAddUser)
    {
      throw new RuntimeException("Unable to create preference due to user");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setDistance(DistanceRange aDistance)
  {
    boolean wasSet = false;
    distance = aDistance;
    wasSet = true;
    return wasSet;
  }

  public boolean setCuisine(Cuisine aCuisine)
  {
    boolean wasSet = false;
    cuisine = aCuisine;
    wasSet = true;
    return wasSet;
  }

  public boolean setPrice(PriceRange aPrice)
  {
    boolean wasSet = false;
    price = aPrice;
    wasSet = true;
    return wasSet;
  }

  public boolean setRating(Rating aRating)
  {
    boolean wasSet = false;
    rating = aRating;
    wasSet = true;
    return wasSet;
  }

  public boolean setPID(String aPID)
  {
    boolean wasSet = false;
    pID = aPID;
    wasSet = true;
    return wasSet;
  }

  public DistanceRange getDistance()
  {
    return distance;
  }

  public Cuisine getCuisine()
  {
    return cuisine;
  }

  public PriceRange getPrice()
  {
    return price;
  }

  public Rating getRating()
  {
    return rating;
  }

  public String getPID()
  {
    return pID;
  }
  /* Code from template association_GetOne */
  public User getUser()
  {
    return user;
  }
  /* Code from template association_SetOneToMany */
  public boolean setUser(User aUser)
  {
    boolean wasSet = false;
    if (aUser == null)
    {
      return wasSet;
    }

    User existingUser = user;
    user = aUser;
    if (existingUser != null && !existingUser.equals(aUser))
    {
      existingUser.removePreference(this);
    }
    user.addPreference(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    User placeholderUser = user;
    this.user = null;
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