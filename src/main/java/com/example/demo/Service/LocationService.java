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

    public Location saveOrUpdate(Long userId,Long taskId, Double lat, Double lon, String city) {//
        Optional<Location> existing = locationRepo.findByUserId(userId);
        Location location = existing.orElse(new Location());

        location.setUserId(userId);
        location.setTaskId(taskId);//
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setCity(city);

        return locationRepo.save(location);
    }

    public Optional<Location> getByUserId(Long userId) {
        return locationRepo.findByUserId(userId);
    }
}
