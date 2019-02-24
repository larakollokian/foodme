package ca.mcgill.ecse428.foodme.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

//import javax.persistence.CascadeType;
import javax.persistence.*;

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

	//User Associations
	private List<Preference> preferences;
	private List<Restaurant> likesAnsDislikes;

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

	@Transient
	@OneToMany(mappedBy = "app_user")
	public List<Restaurant> getLikesAnsDislikes() {
		return likesAnsDislikes;
	}

	public void setLikesAnsDislikes(List<Restaurant> likesAnsDislikes) {
		this.likesAnsDislikes = likesAnsDislikes;
	}

	public void addLikesAnsDislike(Restaurant likesAnsDislike)
	{
		this.likesAnsDislikes.add(likesAnsDislike);
	}
	
	public boolean removeLikesAnsDislike(Restaurant likesAnsDislike)
	{
		if(this.likesAnsDislikes.contains(likesAnsDislike))
		{
			this.likesAnsDislikes.remove(likesAnsDislike);
			return true;
		}
		return false;
	}

	@Transient
	@OneToMany(mappedBy = "app_user")
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

		if(this.preferences == null)
		{
			this.preferences = new ArrayList<Preference>();
		}
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

	@Override
	public String toString() {
		return "AppUser [username=" + username + ", firstName=" + firstName + ", lastName=" + lastName + ", email="
				+ email + ", password=" + password + ", preferences=" + preferences + ", likesAnsDislikes="
				+ likesAnsDislikes + "]";
	}

}