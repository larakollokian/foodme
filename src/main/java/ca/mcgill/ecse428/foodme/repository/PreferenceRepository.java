package ca.mcgill.ecse428.foodme.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import ca.mcgill.ecse428.foodme.exception.*;
import ca.mcgill.ecse428.foodme.model.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Repository
public class PreferenceRepository {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Method that allows get a user given its username
	 * @param username
	 * @return AppUser
	 * @throws  NullObjectException
	 */
	@Transactional
	public AppUser getAppUser(String username) throws NullObjectException {
		if(entityManager.find(AppUser.class, username) == null) {
			throw new NullObjectException("User does not exist");
		}
		else {
			AppUser appUser = entityManager.find(AppUser.class, username);
			return appUser;
		}
	}

    /**
     * Method that gets all preferences in database regardless of user
     * @return list of Preference
     */
    @Transactional
    public List<Preference> getAllPreferences() throws NullObjectException{
        Query q = entityManager.createNativeQuery("SELECT * FROM preferences");
        @SuppressWarnings("unchecked")
        List<Preference> preferences = q.getResultList();
        if(preferences.isEmpty()){
        	throw new NullObjectException("No preferences exists");
		}
        return preferences;
    }

    /**
     * Method that gets all the preferences of a specific user
     * @param username
     * @return list of Preference
	 * @throws NullObjectException
     */
    @Transactional
    public List<Preference> getPreferencesForUser(String username) throws NullObjectException {
        Query q = entityManager.createNativeQuery("SELECT * FROM preferences WHERE app_user_username =:username");
        q.setParameter("username", username);
        @SuppressWarnings("unchecked")
        List<Preference> preferences = q.getResultList();
        if (preferences.size() == 0){
        	throw new NullObjectException ("User does not have preferences");
		}
        return preferences;
    }

	/**
	 * Method that gets the pid of a preference (for controller testing)
	 * @param username
	 * @return pid
	 */
    @Transactional
	public int getPreferenceIDs(String username){
		Query q = entityManager.createNativeQuery("SELECT pid FROM preferences WHERE app_user_username ='johnsmith'");
		if(q.getResultList().size() == 1) {
			return (int) q.getResultList().get(0);
		}
		return 0;
	}

	/**
	 * Method that gets a preference from a pID
	 * @param pID
	 * @return Preference
	 * @throws NullObjectException
	 */
    @Transactional
    public Preference getPreference(int pID) throws NullObjectException{
        if(entityManager.find(Preference.class, pID) == null){
            throw new NullObjectException("Preference with pID "+pID+" does not exist");
        }
        Preference preference = entityManager.find(Preference.class, pID);
        return preference;
    }

	/**
	 * Method that creates a preference
	 * @param username
	 * @param location
	 * @param cuisine
	 * @param price
	 * @param sortBy
	 * @return Preference
	 * @throws NullObjectException
	 */
	@Transactional
	public Preference createPreference(String username, String location, String cuisine, String price, String sortBy) throws NullObjectException{
		AppUser user = getAppUser(username);
		Preference preference = new Preference();
		preference.setLocation(location);
        preference.setCuisine(cuisine);
        preference.setPrice(price);
        preference.setSortBy(sortBy);
		preference.setUser(user);
		entityManager.persist(preference);
		return preference;
	}
	/**
	 * Method that edits a preference of a user
	 * @param username (user Username)
	 * @param pID (preference ID)
	 * @param location
	 * @param cuisine
	 * @param price
	 * @param sortBy
	 * @return Preference
	 * @throws NullObjectException
	 */
	@Transactional
	public Preference editPreference(String username, int pID, String location, String cuisine, String price, String sortBy) throws NullObjectException {
		Preference editPreference = getPreference(pID);
		if(!editPreference.getUser().getUsername().equals(username)){
			throw new NullObjectException("Preference is not related to user");
		}
		editPreference.setLocation(location);
		editPreference.setCuisine(cuisine);
		editPreference.setPrice(price);
		editPreference.setSortBy(sortBy);
		entityManager.merge(editPreference);
		return editPreference;
	}

	/**
	 * Method that deletes a preference given its pID
	 * @param  pID (preference ID)
	 * @return Preference
	 */
	@Transactional
	public Preference deletePreference(String username,int pID) throws NullObjectException {
		Preference p = getPreference(pID);
		if(!p.getUser().getUsername().equals(username)){
			throw new NullObjectException("Preference is not related to user");
		}
		entityManager.remove(p);
		return p;
	}

}


