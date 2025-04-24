package com.example.demo.Controller;
import com.example.demo.Service.UserService;
import com.example.demo.model.UserRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userservice;

    @PostMapping("/reg")
    public UserRegistration refuser(@RequestBody UserRegistration userReg)
    {
       return userservice.create(userReg);

    }

    @GetMapping("/users")
    public List<UserRegistration> getUsers() {
        return userservice.getAllUsers();}




}
