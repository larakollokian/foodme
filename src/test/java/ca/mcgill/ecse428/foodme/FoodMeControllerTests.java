package ca.mcgill.ecse428.foodme;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import ca.mcgill.ecse428.foodme.controller.Controller;
import ca.mcgill.ecse428.foodme.repository.FoodmeRepository;
import ca.mcgill.ecse428.foodme.service.AuthenticationService;
import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class FoodMeControllerTests {

	@LocalServerPort
	int port;
	
    @Autowired
    private AuthenticationService authentication;
	
	@InjectMocks
	Controller controller;
	
    @Autowired
    private WebApplicationContext context;

    @Autowired
	private MockMvc mockMvc;
    
    @MockBean 
    FoodmeRepository repository;
	
//	@Mock
//	FoodmeRepository repository = Mockito.mock(FoodmeRepository.class);
    
    @Before
    public void setup() throws Exception {
    	RestAssured.baseURI="http://localhost/";
    	RestAssured.port=8080;
    }
    
    @Test
    public void testControllerListAllLiked() throws Exception {
    	String username = "Marine";

    	RestAssuredMockMvc.given().
    	param("user", username).
    	log().all().
    	when().
    	get("/users/{user}/allliked/").
    	then().
    	statusCode(HttpStatus.OK.value());
    }
}
