/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4408.bc0cac3a4 modeling language!*/

package ca.mcgill.ecse428.foodme.model;

// line 21 "../../../../../../../ump/tmp570555/model.ump"
// line 51 "../../../../../../../ump/tmp570555/model.ump"
public class Restaurant
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

	enum Cuisine {chinese, italian, french, mexican}
	Cuisine cuisine;
	
	enum Distance {five, ten}
	Distance distance;
	
  //Restaurant Attributes
  private String id;
  private String price;
  private String typeCuisine;
  private String term;
  private String location;
  private double latitude;
  private double longitude;
  private int radius;
  private String categories;
  private int limit;
  private String sort_by;
  private boolean isOpen;
  private int openAt;
  private int rating;

  //Restaurant Associations
  private User user;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Restaurant(String aId, String aPrice, String aTypeCuisine, String aTerm, String aLocation, double aLatitude, double aLongitude, int aRadius, String aCategories, int aLimit, String aSort_by, boolean aIsOpen, int aOpenAt, int aRating, User aUser)
  {
    id = aId;
    price = aPrice;
    typeCuisine = aTypeCuisine;
    term = aTerm;
    location = aLocation;
    latitude = aLatitude;
    longitude = aLongitude;
    radius = aRadius;
    categories = aCategories;
    limit = aLimit;
    sort_by = aSort_by;
    isOpen = aIsOpen;
    openAt = aOpenAt;
    rating = aRating;
    boolean didAddUser = setUser(aUser);
    if (!didAddUser)
    {
      throw new RuntimeException("Unable to create restaurant due to user");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setId(String aId)
  {
    boolean wasSet = false;
    id = aId;
    wasSet = true;
    return wasSet;
  }

  public boolean setPrice(String aPrice)
  {
    boolean wasSet = false;
    price = aPrice;
    wasSet = true;
    return wasSet;
  }

  public boolean setTypeCuisine(String aTypeCuisine)
  {
    boolean wasSet = false;
    typeCuisine = aTypeCuisine;
    wasSet = true;
    return wasSet;
  }

  public boolean setTerm(String aTerm)
  {
    boolean wasSet = false;
    term = aTerm;
    wasSet = true;
    return wasSet;
  }

  public boolean setLocation(String aLocation)
  {
    boolean wasSet = false;
    location = aLocation;
    wasSet = true;
    return wasSet;
  }

  public boolean setLatitude(double aLatitude)
  {
    boolean wasSet = false;
    latitude = aLatitude;
    wasSet = true;
    return wasSet;
  }

  public boolean setLongitude(double aLongitude)
  {
    boolean wasSet = false;
    longitude = aLongitude;
    wasSet = true;
    return wasSet;
  }

  public boolean setRadius(int aRadius)
  {
    boolean wasSet = false;
    radius = aRadius;
    wasSet = true;
    return wasSet;
  }

  public boolean setCategories(String aCategories)
  {
    boolean wasSet = false;
    categories = aCategories;
    wasSet = true;
    return wasSet;
  }

  public boolean setLimit(int aLimit)
  {
    boolean wasSet = false;
    limit = aLimit;
    wasSet = true;
    return wasSet;
  }

  public boolean setSort_by(String aSort_by)
  {
    boolean wasSet = false;
    sort_by = aSort_by;
    wasSet = true;
    return wasSet;
  }

  public boolean setIsOpen(boolean aIsOpen)
  {
    boolean wasSet = false;
    isOpen = aIsOpen;
    wasSet = true;
    return wasSet;
  }

  public boolean setOpenAt(int aOpenAt)
  {
    boolean wasSet = false;
    openAt = aOpenAt;
    wasSet = true;
    return wasSet;
  }

  public boolean setRating(int aRating)
  {
    boolean wasSet = false;
    rating = aRating;
    wasSet = true;
    return wasSet;
  }

  public String getId()
  {
    return id;
  }

  public String getPrice()
  {
    return price;
  }

  public String getTypeCuisine()
  {
    return typeCuisine;
  }

  public String getTerm()
  {
    return term;
  }

  public String getLocation()
  {
    return location;
  }

  public double getLatitude()
  {
    return latitude;
  }

  public double getLongitude()
  {
    return longitude;
  }

  public int getRadius()
  {
    return radius;
  }

  public String getCategories()
  {
    return categories;
  }

  public int getLimit()
  {
    return limit;
  }

  public String getSort_by()
  {
    return sort_by;
  }

  public boolean getIsOpen()
  {
    return isOpen;
  }

  public int getOpenAt()
  {
    return openAt;
  }

  public int getRating()
  {
    return rating;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isIsOpen()
  {
    return isOpen;
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
      existingUser.removeRestaurant(this);
    }
    user.addRestaurant(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    User placeholderUser = user;
    this.user = null;
    if(placeholderUser != null)
    {
      placeholderUser.removeRestaurant(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "id" + ":" + getId()+ "," +
            "price" + ":" + getPrice()+ "," +
            "typeCuisine" + ":" + getTypeCuisine()+ "," +
            "term" + ":" + getTerm()+ "," +
            "location" + ":" + getLocation()+ "," +
            "latitude" + ":" + getLatitude()+ "," +
            "longitude" + ":" + getLongitude()+ "," +
            "radius" + ":" + getRadius()+ "," +
            "categories" + ":" + getCategories()+ "," +
            "limit" + ":" + getLimit()+ "," +
            "sort_by" + ":" + getSort_by()+ "," +
            "isOpen" + ":" + getIsOpen()+ "," +
            "openAt" + ":" + getOpenAt()+ "," +
            "rating" + ":" + getRating()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "user = "+(getUser()!=null?Integer.toHexString(System.identityHashCode(getUser())):"null");
  }
}