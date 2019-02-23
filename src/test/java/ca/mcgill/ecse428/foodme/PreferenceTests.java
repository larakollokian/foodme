package ca.mcgill.ecse428.foodme;

import ca.mcgill.ecse428.foodme.model.AppUser;
import ca.mcgill.ecse428.foodme.model.Preference;
import ca.mcgill.ecse428.foodme.model.PriceRange;
import ca.mcgill.ecse428.foodme.repository.FoodmeRepository;
import ca.mcgill.ecse428.foodme.repository.InvalidInputException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodmeApplication.class)
public class PreferenceTests {
    private AppUser appUser;

    @Mock
    EntityManager entityManager;

    @Autowired
    private FoodmeRepository foodmeRepository;

    @Before
    public void setMockOutput() throws InvalidInputException {
        try {
            appUser = foodmeRepository.createAccount("Tester123", "Test", "User", "student@mcgill.ca", "password");
        }
        catch(Exception e){
            throw new InvalidInputException("User not created.");
        }
    }
    @Test
    public void testAddPreference() {
        assertEquals(1, foodmeRepository.getAllUsers().size());

        appUser = foodmeRepository.getAppUser("Tester123");
        String distanceRange = "fivehundred";
        String cuisine = "Italian";
        String priceRange = "$$$";
        String rating = "four";

        foodmeRepository.createPreference(appUser, priceRange, distanceRange, cuisine, rating);
        assertEquals(appUser.getPreferences().size(), 1);
    }

//    @Test
//    public void testEditPreference() {
//        Preference editPreference = appUser.getPreferences().get(0);
//        String distanceRange = "fivehundred";
//        String cuisine = "Mexican";
//        String priceRange = "$";
//        String rating = "four";
//
//        foodmeRepository.editPreference(appUser, editPreference, priceRange, distanceRange, cuisine, rating, 0);
//        assertEquals(appUser.getPreferences().get(0).getPrice(), PriceRange.$);
//    }

}
