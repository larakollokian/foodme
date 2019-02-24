package ca.mcgill.ecse428.foodme;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import ca.mcgill.ecse428.foodme.model.AppUser;
import ca.mcgill.ecse428.foodme.repository.FoodmeRepository;
import ca.mcgill.ecse428.foodme.repository.InvalidInputException;
import ca.mcgill.ecse428.foodme.security.Password;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodmeApplication.class)
public class ListAllLikedTest {

	private static final String USERNAME = "test";
	private static final String FIRSTNAME = "John";
	private static final String LASTNAME="Doe";
	private static String EMAIL="johnDoe@hotmail.ca";
	private String PASSWORD = "HelloWorld123";
	
	@MockBean
	private FoodmeRepository foodMeRepository;
	
	@Before
	public void setUp() throws InvalidInputException {
	    try {
            AppUser user = new AppUser();
            user.setUsername(USERNAME);
            user.setLastName(LASTNAME);
            user.setFirstName(FIRSTNAME);
            user.setPassword(Password.getSaltedHash(PASSWORD));
            user.setEmail(EMAIL);
            user.addLike("12");
            user.addLike("13");
            
            assertEquals(2, user.getLikes().size());

            Mockito.when(foodMeRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD)).thenReturn(user);
            Mockito.when(foodMeRepository.getAppUser(USERNAME)).thenReturn(user);
            Mockito.when(foodMeRepository.getAppUser("none")).thenReturn(null);
        }
	    catch(Exception e){
	        throw new InvalidInputException("not created");
        }
	}
	
	@After
	public void tearDown() {
		//user.delete();
	}
	
	@Test
	public void listAll () throws InvalidInputException {
		AppUser user;
	    List<String> liked = foodMeRepository.listAllLiked(USERNAME);
	    user = foodMeRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
	    assertEquals(2, user.getLikes().size());
	}
	
	@Test
	//TODO addLiked() method does not update make changes later
	public void listAllAddLiked() throws InvalidInputException{
		AppUser user;
	    List<String> liked = foodMeRepository.listAllLiked(USERNAME);
	    user = foodMeRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
	    assertEquals(2, user.getLikes().size());
	    //foodMeRepository.addLiked(USERNAME, "12222");
	    user.addLike("12222");
	    assertEquals(3, user.getLikes().size());
	}
}
