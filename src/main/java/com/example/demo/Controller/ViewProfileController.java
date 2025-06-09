package com.example.demo.Controller;

import com.example.demo.DTO.ViewProfileDTO;
import com.example.demo.model.Location;
import com.example.demo.model.UserRegistration;
import com.example.demo.model.Worker;
import com.example.demo.repository.LocationRepo;
import com.example.demo.repository.RegUser;
import com.example.demo.repository.WorkerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@RestController
@RequestMapping("/api/view-profile")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ViewProfileController {

    @Autowired
    private RegUser regUser;

    @Autowired
    private WorkerRepo workerRepo;

    @Autowired
    private LocationRepo locationRepo;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getWorkerProfile(@PathVariable Long userId) {
        Optional<UserRegistration> userOpt = regUser.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        UserRegistration user = userOpt.get();
        Optional<Worker> workerOpt = workerRepo.findByUserId(userId);
        Optional<Location> locationOpt = locationRepo.findByUserId(userId);

        Worker worker = workerOpt.orElse(null);
        Location location = locationOpt.orElse(null);

        ViewProfileDTO dto = new ViewProfileDTO(user, worker, location);
        return ResponseEntity.ok(dto);
    }
}
