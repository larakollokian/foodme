/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4408.bc0cac3a4 modeling language!*/

package ca.mcgill.ecse428.foodme.model;

// line 13 "../../../../../../../ump/tmp570555/model.ump"
// line 46 "../../../../../../../ump/tmp570555/model.ump"
public class Preference
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Preference Attributes
  private String range;
  private String distance;
  private String cuisine;
  private boolean open;
  private int rating;

  //Preference Associations
  private User user;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Preference(String aRange, String aDistance, String aCuisine, boolean aOpen, int aRating, User aUser)
  {
    range = aRange;
    distance = aDistance;
    cuisine = aCuisine;
    open = aOpen;
    rating = aRating;
    boolean didAddUser = setUser(aUser);
    if (!didAddUser)
    {
      throw new RuntimeException("Unable to create preference due to user");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setRange(String aRange)
  {
    boolean wasSet = false;
    range = aRange;
    wasSet = true;
    return wasSet;
  }

  public boolean setDistance(String aDistance)
  {
    boolean wasSet = false;
    distance = aDistance;
    wasSet = true;
    return wasSet;
  }

  public boolean setCuisine(String aCuisine)
  {
    boolean wasSet = false;
    cuisine = aCuisine;
    wasSet = true;
    return wasSet;
  }

  public boolean setOpen(boolean aOpen)
  {
    boolean wasSet = false;
    open = aOpen;
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

  public String getRange()
  {
    return range;
  }

  public String getDistance()
  {
    return distance;
  }

  public String getCuisine()
  {
    return cuisine;
  }

  public boolean getOpen()
  {
    return open;
  }

  public int getRating()
  {
    return rating;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isOpen()
  {
    return open;
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
            "range" + ":" + getRange()+ "," +
            "distance" + ":" + getDistance()+ "," +
            "cuisine" + ":" + getCuisine()+ "," +
            "open" + ":" + getOpen()+ "," +
            "rating" + ":" + getRating()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "user = "+(getUser()!=null?Integer.toHexString(System.identityHashCode(getUser())):"null");
  }
}