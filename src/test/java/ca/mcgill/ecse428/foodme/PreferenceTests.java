package ca.mcgill.ecse428.foodme;

import ca.mcgill.ecse428.foodme.model.*;
import ca.mcgill.ecse428.foodme.repository.FoodmeRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
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

    @Test
    public void testAddPreference() {
        if(foodmeRepository.getAppUser("Tester123") == null)
            appUser = foodmeRepository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
        else
            appUser = foodmeRepository.getAppUser("Tester123");
        String distanceRange = "fivehundred";
        String cuisine = "Italian";
        String priceRange = "$$$";
        String rating = "four";

        foodmeRepository.createPreference(appUser, priceRange, distanceRange, cuisine, rating);
        assertEquals(appUser.getPreferences().size(), 1);
    }

    @Test
    public void testEditPreference() {
        Preference editPreference = appUser.getPreferences().get(0);
        String distanceRange = "fivehundred";
        String cuisine = "Mexican";
        String priceRange = "$";
        String rating = "four";

        foodmeRepository.editPreference(appUser, editPreference, priceRange, distanceRange, cuisine, rating, 0);
        assertEquals(appUser.getPreferences().get(0).getPrice(), PriceRange.$);
    }
}