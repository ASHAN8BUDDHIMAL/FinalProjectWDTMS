package com.example.demo.Controller;

import com.example.demo.Service.UserService;

import com.example.demo.model.UserRegistration;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userservice;



    @PostMapping("/reg")
    public UserRegistration refuser(@RequestBody UserRegistration userReg) {
        return userservice.create(userReg);
    }

    @GetMapping("/users")
    public List<UserRegistration> getUsers() {
        return userservice.getAllUsers();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        UserRegistration user = userservice.findByEmailAndPassword(email, password);
        if (user != null) {
            Map<String, String> response = new HashMap<>();
            response.put("userType", user.getUserType());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userservice.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }


}

