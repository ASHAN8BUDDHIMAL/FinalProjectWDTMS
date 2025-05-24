package com.example.demo.Controller;

import com.example.demo.Service.UserService;
import com.example.demo.model.UserRegistration;
import com.example.demo.model.Worker;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class

UserController {

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
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        UserRegistration user = userservice.findByEmailAndPassword(email, password);
        if (user != null) {
            session.setAttribute("loggedInUserId", user.getId());
            return ResponseEntity.ok(Map.of(
                    "email", user.getEmail(),
                    "userType", user.getUserType(),
                    "firstName", user.getFirstName()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {

        Long userId = (Long) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        return userservice.findById(userId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }

    @PostMapping("/{id}/uploadProfilePic")
    public ResponseEntity<String> uploadProfilePic(@PathVariable Long id,
                                                   @RequestParam("file") MultipartFile file) {
        try {
            userservice.uploadProfilePicture(id, file);
            return ResponseEntity.ok("Profile picture uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image.");
        }
    }

    @GetMapping("/profilePic")
    public ResponseEntity<byte[]> getProfilePic(@RequestParam String email) {
        byte[] image = userservice.getProfilePictureByEmail(email);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // or IMAGE_PNG
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    @PostMapping("/worker")
    public ResponseEntity<?> saveWorker(@RequestBody Worker worker, HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        if (userId == null) return ResponseEntity.status(401).body("User not logged in");

        String message = userservice.saveWorkerDetails(userId, worker);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/worker")
    public ResponseEntity<?> updateWorker(@RequestBody Worker worker, HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        if (userId == null) return ResponseEntity.status(401).body("User not logged in");

        String message = userservice.updateWorkerDetails(userId, worker);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/worker")
    public ResponseEntity<?> getWorker(HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        if (userId == null) return ResponseEntity.status(401).body("User not logged in");

        Worker worker = userservice.getWorkerDetails(userId);
        if (worker == null) return ResponseEntity.status(404).body("Worker not found");

        return ResponseEntity.ok(worker);
    }
}














