package ca.mcgill.ecse428.foodme;

import ca.mcgill.ecse428.foodme.model.*;
import ca.mcgill.ecse428.foodme.repository.FoodmeRepository;
import ca.mcgill.ecse428.foodme.repository.InvalidInputException;

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

import java.text.ParseException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodmeApplication.class)
public class AppUserTests {

    private AppUser appUser;

    @Mock
    EntityManager entityManager;

    @Autowired
    private FoodmeRepository foodmeRepository;
    
    @Test
    public void testCreateUser() {
        if(foodmeRepository.getAppUser("Tester123") == null)
            appUser = foodmeRepository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
        else
            appUser = foodmeRepository.getAppUser("Tester123");
        
        String username = appUser.getUsername();
        //    appUser = foodmeRepository.deleteUser(username);
        assertEquals("Tester123", username);
    }
//didn't pass
    @Test
    public void testDeleteUser()  {
        if(foodmeRepository.getAppUser("Tester123") == null)
            appUser = foodmeRepository.testCreateUser("Tester123", "Test", "User", "student@mcgill.ca", "password");
        else
            appUser = foodmeRepository.getAppUser("Tester123");
        
        int before = foodmeRepository.getNumberUsers();

		 foodmeRepository.deleteUser("Tester123");
     
        int after = foodmeRepository.getNumberUsers();

        //    appUser = foodmeRepository.deleteUser(username);
        assertEquals(before,after+1);
    }

}
