package com.example.demo.Service;

import com.example.demo.repository.RegUser;
import com.example.demo.repository.WorkerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class FindWorkerService {

    @Autowired
    private WorkerRepo workerRepo;

    @Autowired
    private RegUser regUser;

    public List<RegUser> findMatchedWorkers(String skill, String location, double minRating) {
        List<Long> regUserIds = workerRepo.findMatchingRegUserIds(skill, location, minRating);
        if (regUserIds.isEmpty()) {
            return new ArrayList<>();
        }
        return regUser.findByIdIn(regUserIds);
    }

}
