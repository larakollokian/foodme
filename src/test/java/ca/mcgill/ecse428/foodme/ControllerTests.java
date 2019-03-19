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
     * */

    @Test
    public void a_testSignUp() throws Exception {
        // sign up and then delete the user
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/create/tester/john/smith/john.smith@gmail.com/helloworld"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User account successfully created.\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void b_testDeleteUser() throws Exception{
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/delete/tester"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User account successfully deleted.\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    /*
    * All tests are on user johnsmith (already exists in database)
    * */

    @Test
    public void c_testLogoutFail() throws Exception {
        //Logout without login
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/logout/johnsmith"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"This user is not logged in\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }
    @Test
    public void d_testLoginWithInvalidPassword() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/auth/johnsmith/none"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"Invalid login password!!!\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void e_testLoginWithInvalidUsername() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/auth/none/none"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"User does not exist\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void f_testLoginSuccess() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/auth/johnsmith/helloworld"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"Login successful\"}";
        JSONAssert.assertEquals(expected, response.getBody(),false);
    }

    @Test
    public void g_testLogoutSuccess() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/logout/johnsmith"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":true,\"message\":\"Logout successful\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void h_testListAllLikedWithNoRestaurants() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/liked"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"message\":\"User does not have liked restaurants\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void i_testAddLiked() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/addliked/vNB5fXTa2bH07lgqSQXv3g/Rotisserie Portugalia"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully liked Restaurant\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void k_testListAllLiked() throws Exception{
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/liked"), HttpMethod.GET, entity, String.class);
        String expected = "[\"vNB5fXTa2bH07lgqSQXv3g\"]";
//        String expected = "[\r\n" + "\"vNB5fXTa2bH07lgqSQXv3g\"\r\n" + "]";
        Assert.assertEquals(expected, response.getBody());
//        JSONAssert.assertEquals(result, response.getBody(), JSONCompareMode.LENIENT);
    }

    @Test
    //Controller method is not implemented yet!!!
    public void l_testRemoveLiked() throws Exception {
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removeliked/vNB5fXTa2bH07lgqSQXv3g"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":true,\"message\":\"User successfully removed liked Restaurant\"}";
    	Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void m_testRemoveLikedNotInLiked() throws Exception {
    	HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/removeliked/vNB5fXTa2bH07lgqSQXv3g"), HttpMethod.POST, entity, String.class);
    	String expected = "{\"response\":false,\"message\":\"Restaurant is not liked by user!!!\"}";
    	Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void o_testListAllDislikedWithNoRestaurants() throws Exception {
        //TO-DO
    }

    @Test
    public void p_testAddDisliked() throws Exception {
        //TO-DO
    }

    @Test
    public void q_testListAllDisliked() throws Exception{
        //TO-DO
    }


    @Ignore
    //Controller method is not implemented yet!!!
    public void r_testRemoveDisliked() throws Exception {
        //TO-DO
    }

    @Ignore
    public void s_testRemoveDislikedNotInDisliked() throws Exception {
        //TO-DO
    }

    @Test
    public void t_testClearEmptyVisited() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/clearvisited"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"Visited list is empty\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void u_testGetAllVisitedEmpty() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/visited"), HttpMethod.GET, entity, String.class);
        String expected = "{\"response\":false,\"message\":\"User hasn't visited a restaurant\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void v_testAddVisited() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/addvisited/RIIOjIdlzRyESw1BkmQHtw/Tacos Et Tortas"), HttpMethod.POST, entity, String.class);
        String expected = "{\"response\":true,\"message\":\"User successfully added to visited list\"}";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void w_testGetAllVisited() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/restaurants/johnsmith/all/visited"), HttpMethod.GET, entity, String.class);
        String expected = "[\"RIIOjIdlzRyESw1BkmQHtw\"]";
        Assert.assertEquals(expected, response.getBody());
    }

    @Test
    public void x_testClearVisited() throws Exception {
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

