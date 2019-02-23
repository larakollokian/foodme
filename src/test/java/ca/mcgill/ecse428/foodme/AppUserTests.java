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

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodmeApplication.class)
public class AppUserTests {

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
    public void testDeleteUser() {
        assertEquals(1, foodmeRepository.getAllUsers().size());

        appUser = foodmeRepository.getAppUser("Tester123");

        String username = appUser.getUsername();
        assertNotEquals(username, null);
        try {
            foodmeRepository.deleteUser(username);
        }
        catch (ParseException e) {
            //Not expected
            e.printStackTrace();
        }
        assertEquals(0, foodmeRepository.getAllUsers().size());


    }

}