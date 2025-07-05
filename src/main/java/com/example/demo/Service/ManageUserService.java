package com.example.demo.Service;

import com.example.demo.model.UserRegistration;
import com.example.demo.repository.ManageUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageUserService {

    @Autowired
    private ManageUser manageUser;

    public List<UserRegistration> getAllUsers() {
        return manageUser.findAll();
    }

    public List<UserRegistration> searchUsers(String keyword) {
        return manageUser.searchUsers(keyword);
    }

    public UserRegistration getUserById(Long id) {
        return manageUser.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserRegistration updateUser(Long id, UserRegistration updatedUser) {
        UserRegistration existing = getUserById(id);
        existing.setFirstName(updatedUser.getFirstName());
        existing.setLastName(updatedUser.getLastName());
        existing.setPhone(updatedUser.getPhone());
        existing.setAddress(updatedUser.getAddress());
        existing.setDistrict(updatedUser.getDistrict());
        existing.setCity(updatedUser.getCity());
        existing.setPostalCode(updatedUser.getPostalCode());
        // Don’t update email/password here unless explicitly needed
        return manageUser.save(existing);
    }

    public void deactivateUser(Long id) {
        UserRegistration user = getUserById(id);
        user.setActive(false);
        manageUser.save(user);
    }

    public void activateUser(Long id) {
        UserRegistration user = getUserById(id);
        user.setActive(true);
        manageUser.save(user);
    }

    public void deleteUser(Long id) {
        manageUser.deleteById(id);
    }
}
