package ca.mcgill.ecse428.foodme;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
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


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}

