package ca.mcgill.ecse428.foodme;

import ca.mcgill.ecse428.foodme.model.AppUser;
import ca.mcgill.ecse428.foodme.model.Preference;
import ca.mcgill.ecse428.foodme.model.PriceRange;
import ca.mcgill.ecse428.foodme.repository.FoodmeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodmeApplication.class)
public class PreferenceTests {

    @Mock
    EntityManager entityManager;

    @Autowired
    private FoodmeRepository foodmeRepository;

    @Test
    public void testAddPreference() {
        AppUser appUser;
        if(foodmeRepository.getAppUser("Tester123") == null) // Create new user if doesn't exist
            appUser = foodmeRepository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
        else
            appUser = foodmeRepository.getAppUser("Tester123");
        String distanceRange = "fivehundred";
        String cuisine = "Italian";
        String priceRange = "$$$";
        String rating = "four";

        foodmeRepository.createPreference(appUser, priceRange, distanceRange, cuisine, rating); // Create new preference
        assertEquals(appUser.getPreferences().size(), 1);
    }

    @Test
    public void testEditPreference() {
        AppUser appUser;
        String username = "Tester123";
        Preference editPreference = null;
        if(foodmeRepository.getAppUser(username) == null) // Create new user if doesn't exist
            appUser = foodmeRepository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
        else
            appUser = foodmeRepository.getAppUser("Tester123");

        Preference newPreference = foodmeRepository.createPreference(appUser, "$$$", "fivehundred", "Italian", "four"); // Create new preference
        int pID = newPreference.getPID(); // Get PID of this new preference

        editPreference = foodmeRepository.getPreference(pID);

        String distanceRange = "fivehundred";
        String cuisine = "Mexican";
        String priceRange = "$";
        String rating = "four";

        editPreference = foodmeRepository.editPreference(editPreference, priceRange, distanceRange, cuisine, rating);
        assertEquals(editPreference.getPrice(), PriceRange.$); // Check to see that the price range changed!
        assertEquals(editPreference.getPID(), pID); // Make sure PID didn't change
    }
    
    @Test
    public void testDeletePreference() {
        AppUser appUser;
        String username = "Tester123";
        Preference aPreference = null;
        if(foodmeRepository.getAppUser(username) == null) // Create new user if doesn't exist
            appUser = foodmeRepository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
        else
            appUser = foodmeRepository.getAppUser("Tester123");

        
        Preference newPreference = foodmeRepository.createPreference(appUser, "$$$", "fivehundred", "Italian", "four"); // Create new preference
        int pID = newPreference.getPID(); // Get PID of this new preference
        int listLengthBefore = foodmeRepository.getPreferencesForUser(username).size();

        Preference deletePreference = foodmeRepository.deletePreference(pID);
        int listLengthAfter = foodmeRepository.getPreferencesForUser(username).size();
        
        assertEquals(listLengthBefore, listLengthAfter+1); // Check to see that the references length

    }
    
    @Test
    public void testDefaultPreference() {
        AppUser appUser;
        String username = "Tester123";
        Preference preference = null;
        if(foodmeRepository.getAppUser(username) == null) // Create new user if doesn't exist
            appUser = foodmeRepository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
        else
            appUser = foodmeRepository.getAppUser("Tester123");

        preference = foodmeRepository.createPreference(appUser, "$$$", "fivehundred", "Italian", "four"); // Create new preference
        int pID = preference.getPID(); // Get PID of this preference

        foodmeRepository.setDefaultPreference(pID, username);
        assertEquals(true, preference.getIsDefault()); 
        
        //set another default preference
        Preference preference2 = foodmeRepository.createPreference(appUser, "$$$", "fivehundred", "Italian", "four");
        foodmeRepository.setDefaultPreference(preference2.getPID(), username);
        
        assertEquals(false, preference.getIsDefault());
        assertEquals(true, preference2.getIsDefault());
    }
    
}
