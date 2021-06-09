package com.anizzzz.testdemo.service;

import com.anizzzz.testdemo.dto.ResponseMessage;
import com.anizzzz.testdemo.model.User;
import com.anizzzz.testdemo.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceUnitTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private List<User> userList;

    @Before
    public void init(){
        userList = Stream.of(
                new User(500, "Ram Shrestha", 55, "Kathmandu"),
                new User(501, "Sita Shrestha", 32, "Pokhara")).collect(Collectors.toList());
    }

    @Test
    public void whenUserListRequested_returnList(){
        when(userRepository.findAll()).thenReturn(userList);

        List<User> users = userService.getAll();
        verify(userRepository, Mockito.times(1)).findAll();
        assertEquals(userList.size(), users.size());
        assertEquals(userList.get(0), users.get(0));
    }

    @Test
    public void whenUserRequestedById_UserPresent(){
        when(userRepository.findById(isA(Integer.class))).thenReturn(Optional.of(userList.get(0)));

        Optional<User> user = userService.getOneById(isA(Integer.class));
        verify(userRepository, Mockito.times(1)).findById(isA(Integer.class));
        Assert.assertTrue(user.isPresent());
        assertEquals(userList.get(0), user.get());
    }

    @Test
    public void whenUserRequestById_UserNotPresent(){
        when(userRepository.findById(isA(Integer.class))).thenReturn(Optional.empty());

        Optional<User> user = userService.getOneById(isA(Integer.class));
        verify(userRepository, Mockito.only()).findById(isA(Integer.class));
        Assert.assertFalse(user.isPresent());
    }

    @Test
    public void whenSavingUserWithUniqueUserTest(){
        User user = userList.get(0);
        when(userRepository.findByName(isA(String.class))).thenReturn(Optional.empty());
        when(userRepository.save(isA(User.class))).thenReturn(isA(User.class));

        ResponseMessage response = userService.save(user);
        verify(userRepository, Mockito.times(1)).save(user);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Saved", response.getMessage());
    }

    @Test
    public void whenSavingUserWithDuplicateNameTest(){
        User user = userList.get(0);
        when(userRepository.findByName(isA(String.class))).thenReturn(Optional.of(user));

        ResponseMessage response = userService.save(user);
        verify(userRepository, Mockito.never()).save(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Duplicate User name.", response.getMessage());
    }

    @Test
    public void whenUpdatingWithPresentUserTest(){
        User user = userList.get(0);
        when(userRepository.findById(isA(Integer.class))).thenReturn(Optional.of(user));
        when(userRepository.save(isA(User.class))).thenReturn(isA(User.class));

        ResponseMessage response = userService.update(user);
        verify(userRepository, Mockito.times(1)).save(user);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Updated", response.getMessage());
    }

    @Test
    public void whenUpdatingWithAbsentUserTest(){
        User user = userList.get(0);
        when(userRepository.findById(isA(Integer.class))).thenReturn(Optional.empty());

        ResponseMessage response = userService.update(user);
        verify(userRepository, Mockito.never()).save(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("User not found.", response.getMessage());
    }

    @Test
    public void whenDeletingWithPresentUserTest(){
        User user = userList.get(0);
        when(userService.getOneById(isA(Integer.class))).thenReturn(Optional.of(user));

        ResponseMessage response = userService.deleteUser(isA(Integer.class));
        verify(userRepository, Mockito.times(1)).delete(user);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Deleted", response.getMessage());
    }

    @Test
    public void whenDeletingWithAbsentUserTest(){
        when(userService.getOneById(isA(Integer.class))).thenReturn(Optional.empty());

        ResponseMessage response = userService.deleteUser(isA(Integer.class));
        verify(userRepository, Mockito.never()).delete(isA(User.class));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }
}
