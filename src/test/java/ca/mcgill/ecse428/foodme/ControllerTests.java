package ca.mcgill.ecse428.foodme;

import org.junit.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ControllerTests {

    @LocalServerPort
    private int port;
    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    /*
     * Letter is added to assure that tests are executing in alphabetic order
     * The reason why I should be executed in this order is because some tests depend on others
     * Ex. You cannot log out if you haven't sign in
     * a - signup/delete
     * b - authentication/logout
     * c - liked restaurants
     * d - disliked restaurants
     * e - visited restaurants
     * */

    @Test
    public void a1_testSignUpWrongEmail() throws Exception {
        // sign up and then delete the user
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/tester/john/smith/john.smithgmail.com/helloworld"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"This is not a valid email address!\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    @Test
    public void a2_testSignUpWrongPasswordSize() throws Exception {
        // sign up and then delete the user
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/tester/john/smith/john.smith@gmail.com/hello"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Your password must be longer than 6 characters!\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    @Test
    public void a3_testSignUpWrongUsername() throws Exception {
        // sign up and then delete the user
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/abc/john/smith/john.smith@gmail.com/helloworld"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Username should be longer than 3 characters\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void a4_testSignUpSuccess() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/tester/john/smith/john.smith@gmail.com/helloworld"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User account successfully created.\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    @Test
    public void a5_testSignUpExistingUser() throws Exception {
        // sign up and then delete the user
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/tester/john/smith/john.smith@gmail.com/helloworld"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"User already exists\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void a6_testDeleteUser() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/delete/tester"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User account successfully deleted.\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void a7_testDeleteNonExistingUser() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/delete/tester"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"User does not exist\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /*
    * All tests are on user johnsmith (already exists in database)
    * */

    @Test
    public void b1_testLogoutFail() throws Exception {
        //Logout without login
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/logout/johnsmith"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"This user is not logged in\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    @Test
    public void b2_testLoginWithInvalidPassword() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/auth/johnsmith/none"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"Invalid login password!!!\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void b3_testLoginWithInvalidUsername() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/auth/none/none"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"User does not exist\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void b4_testLoginSuccess() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/auth/johnsmith/helloworld"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Login successful\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    @Test
    public void b5_testLogoutSuccess() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/logout/johnsmith"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":true,\"message\":\"Logout successful\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void c1_testListAllLikedWithNoRestaurants() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/liked"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"User does not have liked restaurants\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void c2_testAddLiked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/addliked/vNB5fXTa2bH07lgqSQXv3g/Rotisserie Portugalia"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully liked Restaurant\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void c3_testAddDislikedWhenLiked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/adddisliked/vNB5fXTa2bH07lgqSQXv3g/Rotisserie Portugalia"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Restaurant is liked by user!!!\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void c4_testListAllLiked() throws Exception{
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/liked"), HttpMethod.GET, entity, String.class);
        String expected = "[[\"vNB5fXTa2bH07lgqSQXv3g\",\"Rotisserie Portugalia\"]]";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void c5_testRemoveLiked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removeliked/vNB5fXTa2bH07lgqSQXv3g"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":true,\"message\":\"User successfully removed liked Restaurant\"}";
    	Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void c6_testRemoveLikedNotInLiked() throws Exception {
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removeliked/vNB5fXTa2bH07lgqSQXv3g"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":false,\"message\":\"Restaurant is not liked by user!!!\"}";
    	Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void d1_testListAllDislikedWithNoRestaurants() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/disliked"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"User does not have disliked restaurants\"}";
        Assert.assertEquals(expected, response.getBody());    }

    @Test
    public void d2_testAddDisliked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/adddisliked/vNB5fXTa2bH07lgqSQXv3g/Rotisserie Portugalia"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully disliked Restaurant\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void d3_testAddLikedWhenDisliked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/addliked/vNB5fXTa2bH07lgqSQXv3g/Rotisserie Portugalia"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Restaurant is disliked by user!!!\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void d4_testListAllDisliked() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/disliked"), HttpMethod.GET, entity, String.class);
        String expected = "[[\"vNB5fXTa2bH07lgqSQXv3g\",\"Rotisserie Portugalia\"]]";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void d5_testRemoveDisliked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removedisliked/vNB5fXTa2bH07lgqSQXv3g"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":true,\"message\":\"User successfully removed disliked Restaurant\"}";
    	Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void d6_testRemoveDislikedNotInDisliked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removedisliked/vNB5fXTa2bH07lgqSQXv3g"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":false,\"message\":\"Restaurant is not on disliked list!!!\"}";
    	Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void e1_testClearEmptyVisited() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/clearvisited"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Visited list is empty\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void e2_testGetAllVisitedEmpty() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/visited"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"User hasn't visited a restaurant\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void e3_testAddVisited() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/addvisited/RIIOjIdlzRyESw1BkmQHtw/Tacos Et Tortas"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully added to visited list\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void e4_testGetAllVisited() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/visited"), HttpMethod.GET, entity, String.class);
        String expected = "[[\"RIIOjIdlzRyESw1BkmQHtw\",\"Tacos Et Tortas\"]]";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void e5_testClearVisited() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/clearvisited"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully cleared visited list\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    
    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
    
}

