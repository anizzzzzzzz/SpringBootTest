package com.anizzzz.testdemo.controller;

import com.anizzzz.testdemo.dto.ResponseMessage;
import com.anizzzz.testdemo.model.User;
import com.anizzzz.testdemo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll(){
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getOneById(@PathVariable(name = "id") int id){
        Optional<User> user = userService.getOneById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> save(@RequestBody User user){
        ResponseMessage response = userService.save(user);
        System.out.println(response.getStatus().toString() + " " + response.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping
    public ResponseEntity<ResponseMessage> update(@RequestBody User user){
        ResponseMessage response = userService.update(user);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> delete(@PathVariable(name = "id") int id){
        ResponseMessage response = userService.deleteUser(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
