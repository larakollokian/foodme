package ca.mcgill.ecse428.foodme.model;
import java.util.*;

//import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="app_user")
public class AppUser
{

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
	// CONSTRUCTOR (SHOULD BE DEFAULT)
	//------------------------

	//  public User(String aUsername, String aFirstName, String aLastName, String aEmail, String aPassword, List<String> aLikes, List<String> aDislikes)
	//  {
	//    username = aUsername;
	//    firstName = aFirstName;
	//    lastName = aLastName;
	//    email = aEmail;
	//    password = aPassword;
	//    likes = aLikes;
	//    dislikes = aDislikes;
	//    preferences = new ArrayList<Preference>();
	//  }

	//------------------------
	// INTERFACE
	//------------------------

	public boolean setUsername(String aUsername)
	{
		boolean wasSet = false;
		this.username = aUsername;
		wasSet = true;
		return wasSet;
	}

	public boolean setFirstName(String aFirstName)
	{
		boolean wasSet = false;
		this.firstName = aFirstName;
		wasSet = true;
		return wasSet;
	}

	public boolean setLastName(String aLastName)
	{
		boolean wasSet = false;
		this.lastName = aLastName;
		wasSet = true;
		return wasSet;
	}

	public boolean setEmail(String aEmail)
	{
		boolean wasSet = false;
		this.email = aEmail;
		wasSet = true;
		return wasSet;
	}

	public boolean setPassword(String aPassword)
	{
		boolean wasSet = false;
		this.password = aPassword;
		wasSet = true;
		return wasSet;
	}

	public boolean setLikes(List<String> aLikes)
	{
		boolean wasSet = false;
		this.likes = aLikes;
		wasSet = true;
		return wasSet;
	}

	public boolean setDislikes(List<String> aDislikes)
	{
		boolean wasSet = false;
		this.dislikes = aDislikes;
		wasSet = true;
		return wasSet;
	}

	@Id
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

	@ElementCollection(targetClass=String.class)
	public List<String> getLikes()
	{
		if(this.likes == null)
		{
			this.likes = new ArrayList<String>();
		}
		return this.likes;
	}
	
	public void addLike(String like)
	{
		this.likes.add(like);
	}
	
	public boolean removeLike(String like)
	{
		if(this.likes.contains(like))
		{
			this.likes.remove(like);
			return true;
		}
		return false;
	}

	@ElementCollection(targetClass=String.class)
	public List<String> getDislikes()
	{
		if(this.dislikes == null)
		{
			this.dislikes = new ArrayList<String>();
		}
		return this.dislikes;
	}
	
	public void addDislike(String dislike)
	{
		this.dislikes.add(dislike);
	}
	
	public boolean removeDisike(String dislike)
	{
		if(this.dislikes.contains(dislike))
		{
			this.dislikes.remove(dislike);
			return true;
		}
		return false;
	}

	@Transient
	@OneToMany(mappedBy = "app_user"/*, cascade = CascadeType.ALL*/)
	public List<Preference> getPreferences()
	{
		if(this.preferences == null)
		{
			this.preferences = new ArrayList<Preference>();
		}
		return this.preferences;
	}

	public int numberOfPreferences()
	{
		int number = this.preferences.size();
		return number;
	}

	public boolean hasPreferences()
	{
		boolean has = this.preferences.size() > 0;
		return has;
	}

	public void addPreference(Preference aPreference)
	{
		this.preferences.add(aPreference);
	}

	public boolean removePreference(Preference aPreference)
	{
		if(this.preferences.contains(aPreference))
		{
			this.preferences.remove(aPreference);
			return true;
		}
		return false;
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