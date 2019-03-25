package ca.mcgill.ecse428.foodme;

import ca.mcgill.ecse428.foodme.repository.AppUserRepository;
import ca.mcgill.ecse428.foodme.repository.PreferenceRepository;
import ca.mcgill.ecse428.foodme.repository.RestaurantRepository;
import org.junit.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;



/**
 * This class serves to test controllers methods
 * AppUserController.java, RestaurantController.java, PreferenceController.java
 *
 * Note:
 * Letter is added to assure that tests are executing in alphabetic order
 * The reason why I should be executed in this order is because some tests depend on others
 * Ex. You cannot log out if you haven't sign in
 * a - signup/delete
 * b - authentication/logout
 * c - create/delete/get restaurants
 * d - liked restaurants
 * e - disliked restaurants
 * f - visited restaurants
 * g - preferences
 * h - get all users/restaurants
 *
 *  All tests are on user johnsmith (already exists in database)
 *
 * */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ControllerTests {

    private int pid;

    @Autowired
    private PreferenceRepository p;

    @Autowired
    private AppUserRepository a;

    @Autowired
    private RestaurantRepository r;

    @LocalServerPort
    private int port;
    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    /**
     * CT sign up with invalid email
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

    /**
     * CT sign up with wrong password size
     * */
    @Test
    public void a2_testSignUpWrongPasswordSize() throws Exception {
        // sign up and then delete the user
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/tester/john/smith/john.smith@gmail.com/hello"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Your password must be longer than 6 characters!\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT sign up with invalid username
     * */
    @Test
    public void a3_testSignUpWrongUsername() throws Exception {
        // sign up and then delete the user
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/abc/john/smith/john.smith@gmail.com/helloworld"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Username should be longer than 3 characters\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT successful sign up
     * */
    @Test
    public void a4_testSignUpSuccess() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/tester/john/smith/john.smith@gmail.com/helloworld"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User account successfully created.\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT sign up with existing username
     * */
    @Test
    public void a5_testSignUpExistingUser() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/tester/john/smith/john.smith@gmail.com/helloworld"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"User already exists\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT change first name - success
     * */
    @Test
    public void a6_testChangeFirstNameSuccess() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/changeFirstName/tester/johnn"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"First Name successfully changed\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT change first name - fail
     * */
    @Test
    public void a7_testChangeFirstNameFail() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/changeFirstName/tester/johnn"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"New first name cannot be the same as current name\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT change first name - fail
     * */
    @Test
    public void a7_testChangeFirstNameFail1() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/changeFirstName/tester/321j213"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"First name should contain only alphabetic characters\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT change last name - success
     * */
    @Test
    public void a8_testChangeLastNameSuccess() throws Exception {
        // sign up and then delete the user
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/changeLastName/tester/smithh"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Last Name successfully changed\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT change last name - fail
     * */
    @Test
    public void a9_testChangeLastNameFail() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/changeLastName/tester/smithh"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"New last name cannot be the same as current name\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT change last name - fail
     * */
    @Test
    public void a9_testChangeLastNameFail1() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/changeLastName/tester/s432sd"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Last name should contain only alphabetic characters\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT change password - success
     * */
    @Test
    public void aa1_testChangePasswordSuccess() throws Exception {
        // sign up and then delete the user
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/changePassword/tester/helloworld/hahahaha"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Password successfully changed\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT change password - fail
     * */
    @Test
    public void aa2_testChangePasswordFail() throws Exception {
        // sign up and then delete the user
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/changePassword/tester/helloworld/hahahaha"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Invalid old password\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);

        response = restTemplate.exchange(
                createURLWithPort("/users/changePassword/tester/hahahaha/hell"), HttpMethod.POST, entity, String.class);
        expected = "{\"response\":false,\"message\":\"Your password should be longer than 6 characters\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT Reset password of the user with randomly generated password - success
     * */
    @Test
    public void aa3_testResetPasswordSuccess() throws Exception {
        // sign up and then delete the user
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/tester/resetPassword/8"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Password successfully reset\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT reset password - fail
     * */
    @Test
    public void aa4_testResetPasswordFail() throws Exception {
        // sign up and then delete the user
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/tester/resetPassword/4"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Your password should be longer than 6 characters\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT delete user
     * */
    @Test
    public void aa5_testDeleteUser() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/delete/tester"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User account successfully deleted.\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    
    /**
     * CT delete non existing user
     * */
    @Test
    public void aa6_testDeleteNonExistingUser() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/delete/tester"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"User does not exist\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    
    /**
     * CT get user success
     * */
    @Test
    public void aa7_testGetUser() throws Exception {
        pid = a.getAppUser("johnsmith").getDefaultPreferenceID();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/get/johnsmith"), HttpMethod.GET, entity, String.class);
        String expected ="{\"username\":\"johnsmith\",\"firstName\":\"john\",\"lastName\":\"smith\",\"email\":\"johnmith@email.com\",\"password\":\"EckmbA2Glp0=$2O1TwnFPtStMlX84WE/b\",\"defaultPreferenceID\":"+pid+",\"likedRestaurants\":[],\"dislikedRestaurants\":[],\"visitedRestaurants\":[]}\n";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT get user fail
     * */
    @Test
    public void aa8_testGetUserFail() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/get/abc"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"User does not exist\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    /**
     * CT logout fail
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

    /**
     * CT login with invalid password
     * */
    @Test
    public void b2_testLoginWithInvalidPassword() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/auth/johnsmith/none"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"Invalid login password!!!\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT login with invalid username
     * */
    @Test
    public void b3_testLoginWithInvalidUsername() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/auth/none/none"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"User does not exist\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT login successful
     * */
    @Test
    public void b4_testLoginSuccess() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/auth/johnsmith/helloworld"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Login successful\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT logout successful
     * */
    @Test
    public void b5_testLogoutSuccess() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/logout/johnsmith"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":true,\"message\":\"Logout successful\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT to create a new restaurant
     * @throws Exception
     */
    @Test
    public void c1_createRestaurant() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/create/TestRestaurant/abcdef"), HttpMethod.POST, entity, String.class);
        String expected ="{\"response\":true,\"message\":\"Restaurant was successfully created.\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT to get a restaurant by the restaurant ID
     */
    @Test
    public void c2_getRestaurantById()  {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/get/abcdef"), HttpMethod.GET, entity, String.class);
        String expected = "[\"abcdef\",\"TestRestaurant\"]";
        Assert.assertEquals(expected, response.getBody());
    }

    /**
     * CT to delete a restaurant
     * @throws Exception
     */
    @Test
    public void c3_deleteRestaurantSuccess() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/deleteRestaurant/abcdef"), HttpMethod.POST, entity, String.class);
        String expected ="{\"response\":true,\"message\":\"Restaurant data was successfully deleted.\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT to delete a restaurant that does not exist
     * @throws Exception
     */
    @Test
    public void c4_deleteRestaurantFail() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/deleteRestaurant/abcdef"), HttpMethod.POST, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"Restaurant does not exist\"}";
        System.out.println(response.getBody());
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }


    /**
     * CT list all liked restaurants but list is empty - fail
     * */
    @Test
    public void d1_testListAllLikedWithNoRestaurants() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/liked"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"User does not have liked restaurants\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT add a liked restaurant to a user - success
     * */
    @Test
    public void d2_testAddLiked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/addliked/controllertestid/controllertestname"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully liked Restaurant\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT add a disliked restaurant when it is liked -fail
     * */
    @Test
    public void d3_testAddDislikedWhenLiked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/adddisliked/controllertestid/controllertestname"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Restaurant is liked by user!!!\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT list all liked restaurant(1) - success
     * */
    @Test
    public void d4_testListAllLiked() throws Exception{

    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/liked"), HttpMethod.GET, entity, String.class);
        String expected = "[[\"controllertestid\",\"controllertestname\"]]";
        Assert.assertEquals(expected, response.getBody());
    }

    /**
     * CT get number of likes of a restaurant - 1
     * */
    @Test
    public void d5_testGetLikes()  {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/controllertestid/liked"), HttpMethod.GET, entity, String.class);
        String expected = "1";
        Assert.assertTrue(response.getBody().contains(expected));
    }

    /**
     * CT remove liked restaurant - success
     * */
    @Test
    public void d6_testRemoveLiked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removeliked/controllertestid"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":true,\"message\":\"User successfully removed liked Restaurant\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT get number of likes of a restaurant - 0
     * */
    @Test
    public void d7_testGetLikesNone() {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/controllertestid/liked"), HttpMethod.GET, entity, String.class);
        String expected = "0";
        Assert.assertTrue(response.getBody().contains(expected));
    }

    /**
     * CT remove liked restaurant that is not liked - fail
     * */
    @Test
    public void d8_testRemoveLikedNotInLiked() throws Exception {
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removeliked/controllertestid"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":false,\"message\":\"Restaurant is not liked by user!!!\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT list all disliked restaurants but the list is empty - fail
     * */
    @Test
    public void e1_testListAllDislikedWithNoRestaurants() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/disliked"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"User does not have disliked restaurants\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /**
     * CT add disliked restaurant to a user - success
     * */
    @Test
    public void e2_testAddDisliked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/adddisliked/controllertestid/controllertestname"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully disliked Restaurant\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT add liked restaurant when it is disliked - fail
     * */
    @Test
    public void e3_testAddLikedWhenDisliked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/addliked/controllertestid/controllertestname"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Restaurant is disliked by user!!!\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT list all disliked restaurants - success
     * */
    @Test
    public void e4_testListAllDisliked() {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/disliked"), HttpMethod.GET, entity, String.class);
        String expected = "[[\"controllertestid\",\"controllertestname\"]]";
        Assert.assertEquals(expected, response.getBody());
    }

    /**
     * CT get number of dislikes of a restaurant - 1
     * */
    @Test
    public void e5_testGetDislikes() {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/controllertestid/disliked"), HttpMethod.GET, entity, String.class);
        String expected = "1";
        Assert.assertTrue(response.getBody().contains(expected));
    }

    /**
     * CT remove disliked restaurant - success
     * */
    @Test
    public void e6_testRemoveDisliked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removedisliked/controllertestid"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":true,\"message\":\"User successfully removed disliked Restaurant\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT get number of dislikes of a restaurant - 0
     * */
    @Test
    public void e7_testGetDislikesNone() {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/controllertestid/disliked"), HttpMethod.GET, entity, String.class);
        String expected = "0";
        Assert.assertTrue(response.getBody().contains(expected));
    }

    /**
     * CT remove disliked restaurant that is not disliked - fail
     * */
    @Test
    public void e8_testRemoveDislikedNotInDisliked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removedisliked/controllertestid"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":false,\"message\":\"Restaurant is not on disliked list!!!\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT clear visited restaurant list that is empty - fail
     * */
    @Test
    public void f1_testClearEmptyVisited() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/clearvisited"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Visited list is empty\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT list all visited restaurant list that is empty - fail
     * */
    @Test
    public void f2_testGetAllVisitedEmpty() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/visited"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"User hasn't visited a restaurant\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT add visited restaurant - success
     * */
    @Test
    public void f3_testAddVisited() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/addvisited/controllertestid/controllertestname"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully added to visited list\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT list all visited restaurants - success
     * */
    @Test
    public void f4_testGetAllVisited()  {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/visited"), HttpMethod.GET, entity, String.class);
        String expected = "[[\"controllertestid\",\"controllertestname\"]]";
        Assert.assertEquals(expected, response.getBody());
    }

    /**
     * CT clear all visited restaurants - success
     * */
    @Test
    public void f5_testClearVisited() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/clearvisited"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully cleared visited list\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT add preference - success
     * */
    @Test
    public void g1_testAddPreference() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/preferences/johnsmith/add?location=Montreal&cuisine=chinese&price=$&sortBy=rating"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Preference successfully created.\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT get preferences of a user - success
     * */
    @Test
    public void g2_testGetPreferencesForUser() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/preferences/johnsmith"), HttpMethod.GET, entity, String.class);
        String expected = "\"chinese\",\"Montreal\",\"$\",\"rating\",\"johnsmith\"";
        Assert.assertTrue(response.getBody().contains(expected));
    }

    /**
     * CT edit preference - success
     * */
    @Test
    public void g3_testEditPreferenceSuccess() throws Exception {
        pid = p.getPreferenceIDs("johnsmith");
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/preferences/johnsmith/edit/"+pid+"?location=Montreal&cuisine=french&price=$$&sortBy=rating"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Preference successfully modified.\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT edit preference - fail
     * */
    @Test
    public void g4_testEditPreferenceFail() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/preferences/johnsmith/edit/1?location=Montreal&cuisine=french&price=$$&sortBy=rating"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Preference is not related to user\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT get default preference - fail
     * */
    @Test
    public void g5_testGetDefaultPreferenceFail() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/johnsmith/getdefault"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"User does not have a default preference\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT set default preference - fail
     * */
    @Test
    public void g6_testSetDefaultPreferenceFail() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/johnsmith/setdefault/1"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"The preference is not related to the user / does not exist\"}";
        JSONAssert.assertEquals(expected,response.getBody(),false);
    }

    /**
     * CT set default preference - success
     * */
    @Test
    public void g7_testSetDefaultPreferenceSuccess() throws Exception {
        pid = p.getPreferenceIDs("johnsmith");
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/johnsmith/setdefault/"+pid), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Preference successfully set to default\"}";
        JSONAssert.assertEquals(expected,response.getBody(), false);
    }

    /**
     * CT get default preference - success
     * */
    @Test
    public void g8_testGetDefaultPreferenceSuccess() {
        pid = p.getPreferenceIDs("johnsmith");
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/johnsmith/getdefault"), HttpMethod.GET, entity, String.class);
        String expected = "[["+pid+",\"french\",\"Montreal\",\"$$\",\"rating\",\"johnsmith\"]]";
        Assert.assertEquals(expected,response.getBody());
    }

    /**
     * CT delete preference - success
     * */
    @Test
    public void g9_testDeletePreferenceSuccess() throws Exception{
        pid = p.getPreferenceIDs("johnsmith");
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/preferences/johnsmith/delete/"+pid), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Preference successfully deleted.\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT delete preference - fail
     * */
    @Test
    public void gg1_testDeletePreferenceFail() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/preferences/johnsmith/delete/1"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Preference is not related to user\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT get all users
     * */
    @Test
    public void h1_testGetAllUsers() throws Exception{
        int pid = a.getAppUser("johnsmith").getDefaultPreferenceID();
        int pid1 = a.getAppUser("raylabs").getDefaultPreferenceID();
        int pid2 = a.getAppUser("yeffo").getDefaultPreferenceID();
        int pid3 = a.getAppUser("alaye").getDefaultPreferenceID();


        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/get/all"), HttpMethod.GET, entity, String.class);

        String expected1 = "[\"johnsmith\","+pid+",\"johnmith@email.com\",\"john\",\"smith\",\"EckmbA2Glp0=$2O1TwnFPtStMlX84WE/b\"]";
        String expected2 = "[\"raylabs\","+pid1+",\"raylabs@mcgill.ca\",\"raylabs\",\"newLName\",\"8PNDbeyJTs0=$K25T2l0I6B+ODCgu1ENf\"]";
        String expected3 = "[\"yeffo\","+pid2+",\"yeffo@email.com\",\"yeffo\",\"yeffo\",\"zDiQDb/qvDg=$31t0ipzzR+8ozU5nNViR\"]";
        String expected4 = "[\"alaye\","+pid3+",\"anthony.l@hotmail.com\",\"ant\",\"lay\",\"4VY7kkO53xw=$mouAINhPbVZeqnJTEF9r\"]";

        Assert.assertTrue(response.getBody().contains(expected1));
        Assert.assertTrue(response.getBody().contains(expected2));
        Assert.assertTrue(response.getBody().contains(expected3));
        Assert.assertTrue(response.getBody().contains(expected4));
    }

    /**
     * CT to get all the restaurants
     */
    @Test
    public void h2_testGetAllRestaurants() {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/get/all"), HttpMethod.GET, entity, String.class);
        String expected = "[\"RIIOjIdlzRyESw1BkmQHtw\",\"Tacos Et Tortas\"],[\"vNB5fXTa2bH07lgqSQXv3g\",\"Rotisserie Portugalia\"],[\"L8MXAFY14EiC_mzFCgmR_g\",\"Domino's Pizza\"]";
        Assert.assertTrue(response.getBody().contains(expected));
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
    
}

