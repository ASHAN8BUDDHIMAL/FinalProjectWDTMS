package com.example.demo.Service;
import com.example.demo.model.Location;
import com.example.demo.repository.LocationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private LocationRepo locationRepo;

    // Save or update worker location (1 per userId)
    public Location saveOrUpdateWorkerLocation(Long userId, Double lat, Double lon, String city) {
        Optional<Location> existing = locationRepo.findByUserId(userId);
        Location location = existing.orElse(new Location());
        location.setUserId(userId);
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setCity(city);
        return locationRepo.save(location);
    }

    public Optional<Location> getWorkerLocation(Long userId) {
        return locationRepo.findByUserId(userId);
    }


}
