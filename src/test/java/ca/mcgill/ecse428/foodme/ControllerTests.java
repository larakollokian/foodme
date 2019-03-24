package ca.mcgill.ecse428.foodme;

import ca.mcgill.ecse428.foodme.repository.PreferenceRepository;
import org.junit.*;
import org.junit.Assert;
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
        // sign up and then delete the user
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/tester/john/smith/john.smith@gmail.com/helloworld"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"User already exists\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    
    /**
     * CT Reset password of the user with randomly generated password
     * */
    @Test
    public void a6_testResetPassword() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/tester/resetPassword/16"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true}";
        JSONAssert.assertEquals(expected, response.getBody(), JSONCompareMode.LENIENT);
    }

    /**
     * CT delete user
     * */
    @Test
    public void a7_testDeleteUser() throws Exception{
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
    public void a8_testDeleteNonExistingUser() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/delete/tester"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"User does not exist\"}";
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
     * @throws Exception
     */
    @Test
    public void c2_getRestaurantById() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/get/abcdef"), HttpMethod.GET, entity, String.class);
        String expected = "[\"abcdef\",\"TestRestaurant\"]";
        Assert.assertEquals(expected, response.getBody());
    }

    /**
     * CT to get all the restaurants
     * @throws Exception
     */
    @Test
    public void c3_getAllRestaurant() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/get/all"), HttpMethod.GET, entity, String.class);
        String expected = "[[\"RIIOjIdlzRyESw1BkmQHtw\",\"Tacos Et Tortas\"],[\"vNB5fXTa2bH07lgqSQXv3g\",\"Rotisserie Portugalia\"],[\"L8MXAFY14EiC_mzFCgmR_g\",\"Domino's Pizza\"],[\"E8RJkjfdcwgtyoPMjQ_Olg\",\"four-barrel-coffee\"],[\"gR9DTbKCvezQlqvD7_FzPw\",\"north-india-restaurant\"],[\"ddee\",\"taco\"],[\"gR9DTbKCvezQlqvD3_FzPw\",\"normandin\"],[\"gR9DTbKCvezQlqvD7_FzP\",\"north-africa-restaurant\"],[\"322\",\"smokeshack\"],[\"teFIIcVgmkx0uWrs_mROzg\",\"Tacomania\"],[\"ey2HUPlc90UulqEvrPyiyw\",\"Guy's Burger Joint\"],[\"Q3A-L3ebXfHGMbZvA7SWAw\",\"Sunny Bowl\"],[\"OaY79nzIvXqz3WzxK1HgSg\",\"Falafel And Kebab\"],[\"YParqyQqRQ350oDlLhaEGw\",\"Hon Sushi\"],[\"ud9ocsQHI7h3zNO7FdOFYQ\",\"Zareen's\"],[\"3MLCZ99s5KcnAIgNpz8gag\",\"In-N-Out Burger\"],[\"lX6DstPH9zxKXgN31VlUdQ\",\"Cucina Venti Restaurant\"],[\"zD3Pm6WgCwViL7Qf693fmg\",\"The Voya Restaurant\"],[\"QEk4Td9-bcQA-5VZl9kmCA\",\"Beta - GWC6\"],[\"g3J-XCEd0--IZUbACAbI2Q\",\"Costco Food Court\"],[\"xY-FrHUYrL8WZ-z3Bvxv3Q\",\"Hanabi Sushi\"],[\"wSWXRx-7kz8Z8ARmxRRktQ\",\"Pho Tran Vu\"],[\"JWiGiOJqhPpMzZM8P5i4yw\",\"Erik's DeliCafé\"],[\"MO_0P8qoflf7mPzyw_5GcA\",\"L&L Hawaiian Barbecue\"],[\"rvM5NOlJffP-_Ew9Q1h2Lw\",\"P.A. & Gargantua\"],[\"Z2NF_xBF-7RqAfu_4EO9ow\",\"Dim Sum Montréal\"],[\"null\",\"Japote\"],[\"9u-nVJmHz_fB6CFW41JI-w\",\"Café 92\"],[\"LsRSCdUHmwMetiHz0eCqhw\",\"Cloud Bistro\"],[\"NL_yEdlxi7ddafyr4bXBFg\",\"Michael's at Shoreline Restaurant\"],[\"Ik76gEiVhgw8i1-d1xhixg\",\"Mom's Tacos\"],[\"xZHlAIoTfvYtfbOMMKK__g\",\"McDonald's\"],[\"ZGfNnmPCZgsN4jr2B2P8bg\",\"Antojitos Estilo de Jaylah's\"],[\"controllertestid\",\"controllertestname\"],[\"abcdef\",\"TestRestaurant\"]]";
        Assert.assertEquals(expected, response.getBody());
    }

    /**
     * CT to delete a restaurant
     * @throws Exception
     */
    @Test
    public void c4_deleteRestaurantSuccess() throws Exception {
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
    public void c5_deleteRestaurantFail() throws Exception {
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
                createURLWithPort("/restaurants/johnsmith/addliked/vNB5fXTa2bH07lgqSQXv3g/Rotisserie Portugalia"), HttpMethod.POST, entity, String.class);
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
                createURLWithPort("/restaurants/johnsmith/adddisliked/vNB5fXTa2bH07lgqSQXv3g/Rotisserie Portugalia"), HttpMethod.POST, entity, String.class);
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
        String expected = "[[\"vNB5fXTa2bH07lgqSQXv3g\",\"Rotisserie Portugalia\"]]";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void g1_testGetLikes() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/vNB5fXTa2bH07lgqSQXv3g/liked"), HttpMethod.GET, entity, String.class);
        int expected = 1;
        Assert.assertEquals(expected,1);
    }

    /**
     * CT remove liked restaurant - success
     * */
    @Test
    public void d5_testRemoveLiked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removeliked/vNB5fXTa2bH07lgqSQXv3g"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":true,\"message\":\"User successfully removed liked Restaurant\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT remove liked restaurant that is not liked - fail
     * */
    @Test
    public void d6_testRemoveLikedNotInLiked() throws Exception {
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removeliked/vNB5fXTa2bH07lgqSQXv3g"), HttpMethod.POST, entity, String.class);
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
                createURLWithPort("/restaurants/johnsmith/adddisliked/vNB5fXTa2bH07lgqSQXv3g/Rotisserie Portugalia"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully disliked Restaurant\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    @Test
    public void g2_testGetDislikes() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/vNB5fXTa2bH07lgqSQXv3g/disliked"), HttpMethod.GET, entity, String.class);
        int expected = 1;
        Assert.assertEquals(expected,1);
    }

    /**
     * CT add liked restaurant when it is disliked - fail
     * */
    @Test
    public void e3_testAddLikedWhenDisliked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/addliked/vNB5fXTa2bH07lgqSQXv3g/Rotisserie Portugalia"), HttpMethod.POST, entity, String.class);
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
        String expected = "[[\"vNB5fXTa2bH07lgqSQXv3g\",\"Rotisserie Portugalia\"]]";
        Assert.assertEquals(expected, response.getBody());
    }

    /**
     * CT remove disliked restaurant - success
     * */
    @Test
    public void e5_testRemoveDisliked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removedisliked/vNB5fXTa2bH07lgqSQXv3g"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":true,\"message\":\"User successfully removed disliked Restaurant\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    /**
     * CT remove disliked restaurant that is not disliked - fail
     * */
    @Test
    public void e6_testRemoveDislikedNotInDisliked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removedisliked/vNB5fXTa2bH07lgqSQXv3g"), HttpMethod.POST, entity, String.class);
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
                createURLWithPort("/restaurants/johnsmith/addvisited/RIIOjIdlzRyESw1BkmQHtw/Tacos Et Tortas"), HttpMethod.POST, entity, String.class);
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
        String expected = "[[\"RIIOjIdlzRyESw1BkmQHtw\",\"Tacos Et Tortas\"]]";
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
    public void g10_testDeletePreferenceFail() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/preferences/johnsmith/delete/1"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Preference is not related to user\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }



    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
    
}

