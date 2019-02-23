package ca.mcgill.ecse428.foodme;

import ca.mcgill.ecse428.foodme.model.AppUser;
import ca.mcgill.ecse428.foodme.repository.FoodmeRepository;
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
public class DislikeRestaurantTests {

    private AppUser appUser;

    @Mock
    EntityManager entityManager;

    @Autowired
    private FoodmeRepository foodmeRepository;

//    @Test
//    public void testCreateAccountEmptyDislikedRestaurants() {
//
//        appUser = foodmeRepository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
//        assertEquals(appUser.getDislikes().size(), 0);
//
//    }
//
//    @Test
//    public void testAddDislikedRestaurant() {
//
//        if(foodmeRepository.getAppUser("Tester123") == null)
//            appUser = foodmeRepository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
//        else
//            appUser = foodmeRepository.getAppUser("Tester123");
//
//        foodmeRepository.addDislike(appUser, "Restaurant");
//        assertEquals(appUser.getDislikes().size(), 1);
//    }
//
//    @Test
//    public void testRemoveDislikedRestaurantThatExists() {
//
//        if(foodmeRepository.getAppUser("Tester123") == null)
//            appUser = foodmeRepository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
//        else
//            appUser = foodmeRepository.getAppUser("Tester123");
//
//        foodmeRepository.addDislike(appUser, "Restaurant");
//        foodmeRepository.removeDislike(appUser, "Restaurant");
//        assertEquals(appUser.getDislikes().size(), 0);
//    }
//
//    @Test
//    public void testRemoveDislikedRestaurantThatDoesNotExist() {
//
//       if(foodmeRepository.getAppUser("Tester123") == null)
//           appUser = foodmeRepository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
//       else
//           appUser = foodmeRepository.getAppUser("Tester123");
//
//       foodmeRepository.addDislike(appUser, "Restaurant");
//       foodmeRepository.removeDislike(appUser, "Restaurant");
//       foodmeRepository.removeDislike(appUser, "Restaurant2");
//       assertEquals(appUser.getDislikes().size(), 0);
//    }
}
