package com.example.demo.Controller;

import com.example.demo.model.Location;
import com.example.demo.Service.LocationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/location/worker")
public class LocationController {

    @Autowired
    private LocationService locationService;

    // GET worker location
    @GetMapping
    public Location getWorkerLocation(HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        if (userId == null) throw new RuntimeException("User not logged in");
        return locationService.getWorkerLocation(userId).orElse(null);
    }

    // PUT worker location
    @PutMapping
    public Location updateWorkerLocation(@RequestBody Map<String, Object> payload, HttpSession session) {
        Long userId = (Long) session.getAttribute("loggedInUserId");
        if (userId == null) throw new RuntimeException("User not logged in");

        Double lat = ((Number) payload.get("latitude")).doubleValue();
        Double lon = ((Number) payload.get("longitude")).doubleValue();
        String city = (String) payload.get("city");

        return locationService.saveOrUpdateWorkerLocation(userId, lat, lon, city);
    }
}
