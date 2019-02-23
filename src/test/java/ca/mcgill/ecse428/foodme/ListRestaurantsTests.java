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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodmeApplication.class)
public class ListRestaurantsTests {

    private AppUser appUser;

    @Mock
    EntityManager entityManager;

    @Autowired
    private FoodmeRepository foodmeRepository;

//    @Test
//    public void testListAllRestaurantsExceptDisliked() {
//        if(foodmeRepository.getAppUser("Tester123") == null)
//            appUser = foodmeRepository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
//        else
//            appUser = foodmeRepository.getAppUser("Tester123");
//
//    }
//
//    @Test
//    public void testListAllRestaurantsExceptDislikedEmpty() {
//        if(foodmeRepository.getAppUser("Tester123") == null)
//            appUser = foodmeRepository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
//        else
//            appUser = foodmeRepository.getAppUser("Tester123");
//    }
}
