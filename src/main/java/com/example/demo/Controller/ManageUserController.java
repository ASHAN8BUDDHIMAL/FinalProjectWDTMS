package com.example.demo.Controller;

import com.example.demo.Service.ManageUserService;
import com.example.demo.model.UserRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")  // ✅ Recommended REST path
@CrossOrigin
public class ManageUserController {
    @Autowired
    private ManageUserService manageUserService; // ✅ Use correct service

    @GetMapping
    public List<UserRegistration> getAllUsers() {
        return manageUserService.getAllUsers();
    }

    @GetMapping("/search")
    public List<UserRegistration> searchUsers(@RequestParam String keyword) {
        return manageUserService.searchUsers(keyword);
    }

    @GetMapping("/{id}")
    public UserRegistration getUser(@PathVariable Long id) {
        return manageUserService.getUserById(id);
    }

    @PutMapping("/update/{id}")
    public UserRegistration updateUser(@PathVariable Long id, @RequestBody UserRegistration user) {
        return manageUserService.updateUser(id, user);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivate(@PathVariable Long id) {
        manageUserService.deactivateUser(id);
        return ResponseEntity.ok("User deactivated");
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<String> activate(@PathVariable Long id) {
        manageUserService.activateUser(id);
        return ResponseEntity.ok("User activated");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        manageUserService.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }
}
