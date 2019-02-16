/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.1.4426.31aab51de modeling language!*/


import java.util.*;

// line 11 "model.ump"
// line 52 "model.ump"
public class User
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

  //User Attributes
  private String username;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private List<String> likes;
  private List<String> dislikes;

  //User Associations
  private List<Preference> preferences;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public User(String aUsername, String aFirstName, String aLastName, String aEmail, String aPassword, List<String> aLikes, List<String> aDislikes)
  {
    username = aUsername;
    firstName = aFirstName;
    lastName = aLastName;
    email = aEmail;
    password = aPassword;
    likes = aLikes;
    dislikes = aDislikes;
    preferences = new ArrayList<Preference>();
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setUsername(String aUsername)
  {
    boolean wasSet = false;
    username = aUsername;
    wasSet = true;
    return wasSet;
  }

  public boolean setFirstName(String aFirstName)
  {
    boolean wasSet = false;
    firstName = aFirstName;
    wasSet = true;
    return wasSet;
  }

  public boolean setLastName(String aLastName)
  {
    boolean wasSet = false;
    lastName = aLastName;
    wasSet = true;
    return wasSet;
  }

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

  public boolean setLikes(List<String> aLikes)
  {
    boolean wasSet = false;
    likes = aLikes;
    wasSet = true;
    return wasSet;
  }

  public boolean setDislikes(List<String> aDislikes)
  {
    boolean wasSet = false;
    dislikes = aDislikes;
    wasSet = true;
    return wasSet;
  }

  public String getUsername()
  {
    return username;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public String getEmail()
  {
    return email;
  }

  public String getPassword()
  {
    return password;
  }

  public List<String> getLikes()
  {
    return likes;
  }

  public List<String> getDislikes()
  {
    return dislikes;
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
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfPreferences()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public Preference addPreference(DistanceRange aDistance, Cuisine aCuisine, PriceRange aPrice, Rating aRating, String aPID)
  {
    return new Preference(aDistance, aCuisine, aPrice, aRating, aPID, this);
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

  public void delete()
  {
    for(int i=preferences.size(); i > 0; i--)
    {
      Preference aPreference = preferences.get(i - 1);
      aPreference.delete();
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "username" + ":" + getUsername()+ "," +
            "firstName" + ":" + getFirstName()+ "," +
            "lastName" + ":" + getLastName()+ "," +
            "email" + ":" + getEmail()+ "," +
            "password" + ":" + getPassword()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "likes" + "=" + (getLikes() != null ? !getLikes().equals(this)  ? getLikes().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "dislikes" + "=" + (getDislikes() != null ? !getDislikes().equals(this)  ? getDislikes().toString().replaceAll("  ","    ") : "this" : "null");
  }
}