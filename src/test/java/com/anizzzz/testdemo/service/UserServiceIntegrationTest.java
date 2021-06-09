package com.anizzzz.testdemo.service;

import com.anizzzz.testdemo.dto.ResponseMessage;
import com.anizzzz.testdemo.model.User;
import com.anizzzz.testdemo.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private List<User> userList;

    @Before
    public void init(){
        userList = userRepository.saveAll(
                Stream.of(
                        new User("Ram Shrestha", 34, "Kathmandu"),
                        new User("Hari Kumar", 45, "Kathmandu"),
                        new User("Shyam Kumar", 25, "Kathmandu"),
                        new User("Hari Bahadur", 25, "Kathmandu")
                ).collect(Collectors.toList()));
    }


    @Test
    public void whenUserListRequested_returnList(){
        List<User> users = userService.getAll();
        assertEquals(userList.size(), users.size());
    }

    @Test
    public void whenUserRequestedById_UserPresent(){
        User mockUser = userList.get(0);
        Optional<User> user = userService.getOneById(mockUser.getId());

        assertTrue(user.isPresent());
        assertEquals(mockUser.getId(), user.get().getId());
        assertEquals(mockUser.getName(), user.get().getName());
        assertEquals(mockUser.getAge(), user.get().getAge());
        assertEquals(mockUser.getAddress(), user.get().getAddress());
    }

    @Test
    public void whenUserRequestById_UserNotPresent(){
        Optional<User> user = userService.getOneById(1000);
        assertFalse(user.isPresent());
    }

    @Test
    public void whenSavingUserWithUniqueUserTest(){
        User user = new User("Test Data", 33, "Birgunj");

        ResponseMessage response = userService.save(user);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Saved", response.getMessage());
    }

    @Test
    public void whenSavingUserWithDuplicateNameTest(){
        User user = userList.get(0);

        ResponseMessage response = userService.save(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Duplicate User name.", response.getMessage());
    }

    @Test
    public void whenUpdatingWithPresentUserTest(){
        User user = userList.get(1);
        user.setAddress("Nepal");
        user.setAge(66);

        ResponseMessage response = userService.update(user);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Updated", response.getMessage());
    }

    @Test
    public void whenUpdatingWithAbsentUserTest(){
        User user = new User(550, "Test Data", 33, "Birgunj");

        ResponseMessage response = userService.update(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("User not found.", response.getMessage());
    }

    @Test
    public void whenDeletingWithPresentUserTest(){
        User user = userList.get(userList.size()-1);
        ResponseMessage response = userService.deleteUser(user.getId());

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Deleted", response.getMessage());
    }

    @Test
    public void whenDeletingWithAbsentUserTest(){
        ResponseMessage response = userService.deleteUser(550);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("User not found.", response.getMessage());
    }

    @After
    public void destroy(){
        userRepository.deleteAll(userRepository.findAll());
    }
}
