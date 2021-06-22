package com.anizzzz.testdemo.controller;

import com.anizzzz.testdemo.dto.ResponseMessage;
import com.anizzzz.testdemo.model.User;
import com.anizzzz.testdemo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.isA;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private List<User> userList;

    @Before
    public void init(){
        userList = Stream
                .of(new User(500, "Ram Shrestha", 34, "Kathmandu"),
                        new User(501, "Hari Kumar", 45, "Kathmandu")).collect(Collectors.toList());
    }

    @Test
    public void getAllUsersTest() throws Exception {
        Mockito.when(userService.getAll()).thenReturn(Collections.emptyList());
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/user")
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        Mockito.verify(userService, Mockito.times(1)).getAll();
    }

    @Test
    public void getUserById_Test() throws Exception {
        User user = userList.get(0);
        Mockito.when(userService.getOneById(user.getId())).thenReturn(Optional.of(user));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/user/{id}", 500)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).getOneById(user.getId());
    }

    @Test
    public void getUserById_NotPresent_Test() throws Exception{
        User user = userList.get(0);
        Mockito.when(userService.getOneById(user.getId())).thenReturn(Optional.empty());
        mockMvc.perform(
                MockMvcRequestBuilders.get("/user/{id}", 400)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void saveUser_Test() throws Exception {
        User user = userList.get(0);
        Mockito.when(userService.save(isA(User.class))).thenReturn(new ResponseMessage("Saved", HttpStatus.OK));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Matchers.is("Saved")));
        Mockito.verify(userService, Mockito.times(1)).save(isA(User.class));
    }

    @Test
    public void whenSaveUser_DuplicateUser_return400() throws Exception{
        User user = userList.get(0);
        Mockito.when(userService.save(isA(User.class))).thenReturn(new ResponseMessage("Duplicate User name.", HttpStatus.BAD_REQUEST));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.is("Duplicate User name.")));
        Mockito.verify(userService, Mockito.times(1)).save(isA(User.class));
    }

    @Test
    public void whenUserUpdate_return200() throws Exception{
        User user = userList.get(0);
        Mockito.when(userService.update(isA(User.class))).thenReturn(new ResponseMessage("Updated", HttpStatus.OK));

        mockMvc.perform(
                MockMvcRequestBuilders.put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Matchers.is("Updated")));
        Mockito.verify(userService, Mockito.times(1)).update(isA(User.class));
    }

    @Test
    public void whenUserUpdate_return400() throws Exception{
        User user = userList.get(0);
        Mockito.when(userService.update(isA(User.class))).thenReturn(new ResponseMessage("User not found.", HttpStatus.BAD_REQUEST));

        mockMvc.perform(
                MockMvcRequestBuilders.put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.is("User not found.")));
        Mockito.verify(userService, Mockito.times(1)).update(isA(User.class));
    }

    @Test
    public void whenUserDelete_return200() throws Exception{
        Mockito.when(userService.deleteUser(isA(Integer.class))).thenReturn(new ResponseMessage("Deleted", HttpStatus.OK));

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/user/{id}", isA(Integer.class))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", Matchers.is("Deleted")));
        Mockito.verify(userService, Mockito.times(1)).deleteUser(isA(Integer.class));
    }

    @Test
    public void whenUserDelete_return400() throws Exception{
        Mockito.when(userService.deleteUser(isA(Integer.class))).thenReturn(new ResponseMessage("User not found.", HttpStatus.BAD_REQUEST));

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/user/{id}", isA(Integer.class))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.is("User not found.")));
        Mockito.verify(userService, Mockito.times(1)).deleteUser(isA(Integer.class));
    }
}
