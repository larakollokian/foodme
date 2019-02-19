package ca.mcgill.ecse428.foodme;

import ca.mcgill.ecse428.foodme.security.*;
import ca.mcgill.ecse428.foodme.repository.*;
import ca.mcgill.ecse428.foodme.service.*;
import ca.mcgill.ecse428.foodme.model.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.After;
import org.junit.Before;

public class TestAuthenticationService {
    
    
    @Autowired
    private FoodmeRepository repository;
    
    private AuthenticationService authService;

	private AppUser user;
	
	@Before
	public void setUp() throws Exception {
		
		authService = new AuthenticationService();
        
        //Create user
		// try {
		// 	//user = repository.createUser("Bob", "asdfasdf");
		// } catch (InvalidInputException e) {
		// 	fail("Users could not be created.");
		// }
		
	}

	@After
	public void tearDown() throws Exception {
		//Remove user
	}
	
	@Test
	public void testLogin() {
		
	
	}
	
	@Test
	public void testLogout() {
       
	}

}
