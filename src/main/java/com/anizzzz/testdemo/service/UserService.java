package com.anizzzz.testdemo.service;

import com.anizzzz.testdemo.dto.ResponseMessage;
import com.anizzzz.testdemo.model.User;
import com.anizzzz.testdemo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public Optional<User> getOneById(int id){
        return userRepository.findById(id);
    }

    public ResponseMessage save(User user){
        Optional<User> user1 = userRepository.findByName(user.getName());
        if(!user1.isPresent()){
            userRepository.save(user);
            return new ResponseMessage("Saved", HttpStatus.OK);
        }
        return new ResponseMessage("Duplicate User name.", HttpStatus.BAD_REQUEST);
    }

    public ResponseMessage update(User user){
        Optional<User> user1 = userRepository.findById(user.getId());
        if(user1.isPresent()){
            userRepository.save(user);
            return new ResponseMessage("Updated", HttpStatus.OK);
        }
        return new ResponseMessage("User not found.", HttpStatus.BAD_REQUEST);
    }

    public ResponseMessage deleteUser(int id){
        Optional<User> user = getOneById(id);
        if(user.isPresent()){
            userRepository.delete(user.get());
            return new ResponseMessage("Deleted", HttpStatus.OK);
        }
        return new ResponseMessage("User not found.", HttpStatus.BAD_REQUEST);
    }
}
