package ca.mcgill.ecse428.foodme;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTests {

    @LocalServerPort
    private int port;
    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @Test
    public void testLoginSuccess() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/auth/johndoe/helloworld"), HttpMethod.GET, entity, String.class);
//        String expected ="{\"response\":true,\"error\":null}";
        String expected = "{\"response\":true,\"message\":\"Login successful\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    @Test
    public void testLoginWithInvalidPassword() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/auth/johndoe/none"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"Invalid login password!!!\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void testLoginWithInvalidUsername() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/auth/none/none"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"User does not exist\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        //Login first
        ResponseEntity<String> response1 = restTemplate.exchange(
                createURLWithPort("/users/auth/johndoe/helloworld"), HttpMethod.GET, entity, String.class);
        String expected1 = "{\"response\":true,\"message\":\"Login successful\"}";
        JSONAssert.assertEquals(expected1, response1.getBody(),false);
        //Logout second
        ResponseEntity<String> response2 = restTemplate.exchange(
                createURLWithPort("/users/logout/johndoe"), HttpMethod.GET, entity, String.class);
        String expected2 ="{\"response\":true,\"message\":\"Logout successful\"}";
        JSONAssert.assertEquals(expected2, response2.getBody(), false);
    }

    @Test
    public void testLogoutFail() throws Exception {
        //Logout without login
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/logout/yeffo"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"This user is not logged in\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    
    @Test
    public void testListAllLiked() throws Exception{
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/yeffo/get/all/liked"), HttpMethod.GET, entity, String.class);
        String result = "[\r\n" + "\"vNB5fXTa2bH07lgqSQXv3g\"\r\n" + "]";
        JSONAssert.assertEquals(result, response.getBody(), JSONCompareMode.LENIENT);
    }
    
    @Test
    public void testListAllLikedWithNoRestaurants() throws Exception {
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/test/get/all/liked"), HttpMethod.GET, entity, String.class);
        Assert.assertTrue(response.toString().contains("User does not have liked restaurants"));
    }

    @Ignore
    @Test
    public void testAddLiked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johndoe/addliked/vNB5fXTa2bH07lgqSQXv3g/Rotisserie Portugalia"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully liked Restaurant\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    //Controller method is not implemented yet!!!
    @Test
    public void testRemoveLiked() throws Exception {
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johndoe/removeliked/vNB5fXTa2bH07lgqSQXv3g"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":true,\"message\":\"User successfully removed liked Restaurant\"}";
    	Assert.assertEquals(expected, response.getBody());
    }
    
    @Ignore
    //@Test
    public void testRemoveLikedNotInLiked() throws Exception {
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johndoe/removeliked/vNB5fqSQXv3g"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":true,\"message\":\"User successfully removed liked Restaurant\"}";
    	Assert.assertEquals(expected, response.getBody());
    }
    
    @Test
    public void testSignUp() throws Exception {
    	// sign up and then delete the user 
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/testUserJS/john/smith/john.smith@gmail.com/helloworld1234"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User account successfully created.\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        
        response = restTemplate.exchange(
                createURLWithPort("/users/delete/testUserJS"), HttpMethod.GET, entity, String.class);
        expected = "{\"response\":true,\"message\":\"User account successfully deleted.\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void testSignUpInvalidPassword() throws Exception {
    	// sign up and then delete the user 
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/testUserJS/john/smith/john.smith@gmail.com/pass"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Your password must be longer than 6 characters!\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        
    }

    @Test
    public void testSignUpInvalidEmail() throws Exception {
    	// sign up and then delete the user 
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/testUserJS/john/smith/john.smithgmail.com/password1234"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"This is not a valid email address!\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        
    }

    /*
    @Test
    public void testSignUpInvalidFirstName() throws Exception {
    	// sign up and then delete the user 
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/testUserJS/ /smith/john.smith@gmail.com/password1234"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Username, firstName and lastName must be at least 1 character\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        
    }

    @Test
    public void testSignUpInvalidLastName() throws Exception {
    	// sign up and then delete the user 
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/testUserJS/john//john.smith@gmail.com/password1234"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Username, firstName and lastName must be at least 1 character\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        
    }

    @Test
    public void testSignUpInvalidUsername() throws Exception {
    	// sign up and then delete the user 
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create//john/smith/john.smith@gmail.com/password1234"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Username, firstName and lastName must be at least 1 character\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        
    }
*/



    @Test
    public void testSignUpExistingUser() throws Exception {
    	// sign up and then create duplicate user, then delete user 
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/testUser/Test/User/test.user@gmail.com/helloworld1234"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User account successfully created.\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);

        response = restTemplate.exchange(
            createURLWithPort("/users/create/testUser/Test/User/test.user@gmail.com/helloworld1234"), HttpMethod.POST, entity, String.class);
        expected = "{\"response\":false,\"message\":\"User already exists\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);

        
        response = restTemplate.exchange(
                createURLWithPort("/users/delete/testUser"), HttpMethod.GET, entity, String.class);
        expected = "{\"response\":true,\"message\":\"User account successfully deleted.\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    
    }

    
    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
    
}

