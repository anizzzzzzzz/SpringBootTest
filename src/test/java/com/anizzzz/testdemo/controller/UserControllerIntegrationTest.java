package com.anizzzz.testdemo.controller;

import com.anizzzz.testdemo.dto.ResponseMessage;
import com.anizzzz.testdemo.model.User;
import com.anizzzz.testdemo.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    private TestRestTemplate restTemplate;
    private HttpHeaders headers;
    private List<User> userList;

    @Before
    public void init(){
        userList = userRepository.saveAll(
                        Stream.of(
                                new User("Ram Shrestha", 34, "Kathmandu"),
                                new User("Hari Kumar", 45, "Kathmandu"),
                                new User("Shyam Kumar", 25, "Kathmandu"),
                                new User("Hari Bahadur", 25, "Kathmandu")
                        ).collect(Collectors.toList())
                );

        restTemplate = new TestRestTemplate();
        headers = new HttpHeaders();
    }

    @Test
    public void getAllUsersTest() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<User[]> response = restTemplate.exchange(
                createURLWithPort("/user"), HttpMethod.GET, entity, User[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(4, response.getBody().length);
    }

    @Test
    public void getUserById_Test(){
        User mockUser = userList.get(0);
        ResponseEntity<User> response = restTemplate.exchange(createURLWithPort("/user/"+mockUser.getId()),
                HttpMethod.GET, null, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        User responseUser = response.getBody();
        assertNotNull(responseUser);
        assertEquals(mockUser.getName(), responseUser.getName());
    }

    @Test
    public void getUserById_NotPresent_Test(){
        ResponseEntity<User> response = restTemplate.exchange(createURLWithPort("/user/500"),
                HttpMethod.GET, null, User.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void saveUser_Test(){
        User user = new User("Test Data", 33, "Pokhara");

        ResponseMessage response = restTemplate
                .postForObject(createURLWithPort("/user"), user, ResponseMessage.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void whenSaveUser_DuplicateUser_return400(){
        User user = new User("Hari Bahadur", 25, "Kathmandu");
        ResponseMessage response = restTemplate.postForObject(createURLWithPort("/user"), user, ResponseMessage.class);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    @Test
    public void whenUserUpdate_return200(){
        User user = userList.get(0);

        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        ResponseEntity<ResponseMessage> response = restTemplate.exchange(
                createURLWithPort("/user"), HttpMethod.PUT, entity, ResponseMessage.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void whenUserUpdate_return400(){
        User user = new User(55,"Not Present", 25, "Kathmandu");

        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        ResponseEntity<ResponseMessage> response = restTemplate.exchange(
                createURLWithPort("/user"), HttpMethod.PUT, entity, ResponseMessage.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void whenUserDelete_return200(){
        User user = userList.get(0);

        ResponseEntity<ResponseMessage> response = restTemplate.exchange(
                createURLWithPort("/user/"+user.getId()), HttpMethod.DELETE, null, ResponseMessage.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void whenUserDelete_return400(){
        ResponseEntity<ResponseMessage> response = restTemplate.exchange(
                createURLWithPort("/user/500"), HttpMethod.DELETE, null, ResponseMessage.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @After
    public void cleanUp(){
        userRepository.deleteAll();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
