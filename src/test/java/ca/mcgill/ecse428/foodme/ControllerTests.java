package ca.mcgill.ecse428.foodme;

import ca.mcgill.ecse428.foodme.controller.PreferenceController;
import ca.mcgill.ecse428.foodme.repository.PreferenceRepository;
import org.junit.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import ca.mcgill.ecse428.foodme.model.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ControllerTests {

    private int pid;

    @Autowired
    private PreferenceRepository p;

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
     * f - preferences
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
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    @Test
    public void c2_testAddLiked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/addliked/vNB5fXTa2bH07lgqSQXv3g/Rotisserie Portugalia"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully liked Restaurant\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    @Test
    public void c3_testAddDislikedWhenLiked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/adddisliked/vNB5fXTa2bH07lgqSQXv3g/Rotisserie Portugalia"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Restaurant is liked by user!!!\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
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
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    @Test
    public void c6_testRemoveLikedNotInLiked() throws Exception {
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removeliked/vNB5fXTa2bH07lgqSQXv3g"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":false,\"message\":\"Restaurant is not liked by user!!!\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    @Test
    public void d1_testListAllDislikedWithNoRestaurants() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/disliked"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"User does not have disliked restaurants\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    @Test
    public void d2_testAddDisliked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/adddisliked/vNB5fXTa2bH07lgqSQXv3g/Rotisserie Portugalia"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully disliked Restaurant\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    @Test
    public void d3_testAddLikedWhenDisliked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/addliked/vNB5fXTa2bH07lgqSQXv3g/Rotisserie Portugalia"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Restaurant is disliked by user!!!\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
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
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    @Test
    public void d6_testRemoveDislikedNotInDisliked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removedisliked/vNB5fXTa2bH07lgqSQXv3g"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":false,\"message\":\"Restaurant is not on disliked list!!!\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    @Test
    public void e1_testClearEmptyVisited() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/clearvisited"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Visited list is empty\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    @Test
    public void e2_testGetAllVisitedEmpty() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/visited"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"User hasn't visited a restaurant\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    @Test
    public void e3_testAddVisited() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/addvisited/RIIOjIdlzRyESw1BkmQHtw/Tacos Et Tortas"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully added to visited list\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
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
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    @Test
    public void f1_testAddPreference() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/preferences/johnsmith/add?location=Montreal&cuisine=chinese&price=$&sortBy=rating"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Preference successfully created.\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }


    @Test
    public void f2_testGetPreferencesForUser() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/preferences/johnsmith"), HttpMethod.GET, entity, String.class);
        String expected = "\"chinese\",\"Montreal\",\"$\",\"rating\",\"johnsmith\"";
        Assert.assertTrue(response.getBody().contains(expected));
    }

    @Test
    public void f3_testEditPreferenceSuccess() throws Exception {
        pid = p.getPreferenceIDs("johnsmith");
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/preferences/johnsmith/edit/"+pid+"?location=Montreal&cuisine=french&price=$$&sortBy=rating"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Preference successfully modified.\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }
    @Test
    public void f4_testEditPreferenceFail() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/preferences/johnsmith/edit/1?location=Montreal&cuisine=french&price=$$&sortBy=rating"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Preference is not related to user\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }
    @Test
    public void f5_testDeletePreferenceSuccess() throws Exception{
        pid = p.getPreferenceIDs("johnsmith");
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/preferences/johnsmith/delete/"+pid), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Preference successfully deleted.\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }
    @Test
    public void f6_testDeletePreferenceFail() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/preferences/johnsmith/delete/1"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Preference is not related to user\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }
    
    @Test
    public void f7_testGetDefaultPreferenceSuccess() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/yeffo/getdefault"), HttpMethod.GET, entity, String.class);
        String expected = "77";
        Assert.assertTrue(response.getBody().contains(expected));
    }
    
    @Test
    public void f8_testGetDefaultPreferenceFail() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/alaye/getdefault"), HttpMethod.GET, entity, String.class);
        String expected = "77";
        Assert.assertEquals(response.getBody().contains(expected),false);
    }
    
    @Test
    public void f9_testSetDefaultPreferenceSuccess() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/yeffo/setdefault/77"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Preference successfully set to default\"}";
        Assert.assertTrue(response.getBody().contains(expected));
    }
    
    @Test
    public void f10_testSetDefaultPreferenceFail() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/yeffo/setdefault/75"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Preference successfully set to default\"}";
        Assert.assertEquals(response.getBody().contains(expected),false);
    }

    /* tests need to be adjusted here
    @Test
    public void g1_testGetLikes() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/Z2NF_xBF-7RqAfu_4EO9ow/liked"), HttpMethod.GET, entity, String.class);
        String expected = "3";
        Assert.assertEquals(response.getBody().contains(expected),3);
    }

    @Test
    public void g2_testGetDislikes() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/Z2NF_xBF-7RqAfu_4EO9ow/disliked"), HttpMethod.GET, entity, String.class);
        String expected = "0";
        Assert.assertEquals(response.getBody().contains(expected),0);
    }
    */

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
    
}

