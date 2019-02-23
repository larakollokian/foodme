package ca.mcgill.ecse428.foodme;

import ca.mcgill.ecse428.foodme.controller.Controller;
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
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import static org.mockito.ArgumentMatchers.anyString;


import org.springframework.beans.factory.annotation.Autowired;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodmeApplication.class)
public class TestAuthenticationService {

    @Mock
    private FoodmeRepository repository;

    @Autowired
    private AuthenticationService authService;

	@InjectMocks
	private Controller controller;


	private AppUser user;

	private static final String USERNAME = "testUsername";
	private static final String FIRSTNAME = "John";
	private static final String LASTNAME="Doe";
	private static String EMAIL="johnDoe@hotmail.ca";
	private String PASSWORD = "HelloWord123";
	private List<String> LIKES;
	private List<String> DISLIKES;
	private List<Preference> PREFERENCES;


//	@Before
//	public void setMockOutput() {
//		when(repository.getAppUser(anyString())).thenAnswer( (InvocationOnMock invocation) -> {
//			if(invocation.getArgument(0).equals(USERNAME)) {
//				AppUser user = new AppUser();
//				user.setFirstName(FIRSTNAME);
//				user.setLastName()
//				user.setPassword(PASSWORD);
//				return user;
//			} else {
//				return null;
//			}
//	}

}

