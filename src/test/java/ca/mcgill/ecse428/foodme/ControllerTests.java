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
                createURLWithPort("/users/auth/raylabs/raylabs"), HttpMethod.GET, entity, String.class);
        //String expected = "{\"username\":raylabs,\"password\":raylabs}";
        String expected ="{\"response\":true,\"error\":null}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void testLoginWithInvalidPassword() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/auth/raylabs/none"), HttpMethod.GET, entity, String.class);
        String expected ="{\"response\":false,\"error\":\"Invalid Password\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void testLoginWithInvalidUsername() throws Exception {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/users/auth/none/none"), HttpMethod.GET, entity, String.class);
        //String expected = "{\"username\":raylabs,\"password\":raylabs}";
        String expected ="{\"response\":false,\"error\": \"User does not exist\"}";
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    
    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}

