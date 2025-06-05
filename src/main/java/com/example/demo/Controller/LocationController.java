package com.example.demo.Controller;

import com.example.demo.Service.LocationService;
import com.example.demo.model.Location;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/location")
    public Location getLocation(HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        Long taskId = (Long) session.getAttribute("createdTaskId");//
        if (userId == null) throw new RuntimeException("User not logged in");
        if (taskId == null) throw new RuntimeException("Task not found in session");


        Optional<Location> location = locationService.getByUserId(userId);
        return location.orElse(null);
    }

    @PutMapping("/location")
    public Location updateLocation(@RequestBody Map<String, Object> payload, HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        Long taskId = (Long) session.getAttribute("createdTaskId");//
        if (userId == null) throw new RuntimeException("User not logged in");

        Double lat = ((Number) payload.get("latitude")).doubleValue();
        Double lon = ((Number) payload.get("longitude")).doubleValue();
        String city = (String) payload.get("city");

        return locationService.saveOrUpdate(userId,taskId, lat, lon, city);//
    }
}
