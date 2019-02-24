package ca.mcgill.ecse428.foodme;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import ca.mcgill.ecse428.foodme.model.AppUser;
import ca.mcgill.ecse428.foodme.repository.FoodmeRepository;
import ca.mcgill.ecse428.foodme.repository.InvalidInputException;
import ca.mcgill.ecse428.foodme.security.Password;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodmeApplication.class)
public class AddLikedUT {
	
    @MockBean
    private FoodmeRepository foodMeRepository;
    
	private static final String USERNAME = "Ali";
	private static final String FIRSTNAME = "Ali";
	private static final String LASTNAME="Baba";
	private static String EMAIL="ali.baba@gmail.com";
	private String PASSWORD = "1234";

	@Before
	public void setUp() throws InvalidInputException {
	    try {
            AppUser user = new AppUser();
            user.setUsername(USERNAME);
            user.setLastName(LASTNAME);
            user.setFirstName(FIRSTNAME);
            user.setPassword(Password.getSaltedHash(PASSWORD));
            user.setEmail(EMAIL);

            assertEquals(0, user.getLikes().size());

            Mockito.when(foodMeRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD)).thenReturn(user);
            Mockito.when(foodMeRepository.getAppUser(USERNAME)).thenReturn(user);
            Mockito.when(foodMeRepository.getAppUser("none")).thenReturn(null);
        }
	    catch(Exception e){
	        e.printStackTrace();
        }
	}
	
	@Test
	public void addLike () throws InvalidInputException {
		AppUser user;
    	String id = "E8RJkjfdcwgtyoPMjQ_Olg";
	    user = foodMeRepository.createAccount(USERNAME, FIRSTNAME, LASTNAME, EMAIL, PASSWORD);
	    foodMeRepository.addLiked(USERNAME, id);
	    assertEquals(1, user.getLikes().size());
	}
    
//    @Ignore
//    public void test () {
//    	String APIKey = "F5ByVWSif5NWb6w3YYAQjRGOI9Xcg8WKqzBDkPnEl4YDneNpsaKn35YcFEqJyvyV_kUTStuTG2n9-Pi9R7-u9GIkmBQY8LjfNJSrAVEs_K5pGJLCAsWc4N3oxGRgXHYx";
//    	String id = "E8RJkjfdcwgtyoPMjQ_Olg";
//    	String username = "Marine";
//    	
//    	
//    	//Response response = (Response)
//        String answer =
//        given().
//        header(HttpHeaders.AUTHORIZATION, "Bearer "+APIKey).
//        param("restaurant", id).
//        log().all().
//        when().
//        get("https://api.yelp.com/v3/businesses/{id}").
//        then().
//        statusCode(HttpStatus.OK.value()).
//        contentType(ContentType.JSON).extract().response();
//        //statusCode(HttpStatus.OK.value()).log().all();
//    }
}
