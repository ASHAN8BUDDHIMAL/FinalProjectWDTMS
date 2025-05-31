package com.example.demo.Controller;

import com.example.demo.Service.CreateTaskService;
import com.example.demo.Service.FindWorkerService;
import com.example.demo.repository.RegUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/find")
public class FindWorkerController {

//    @Autowired
//    private FindWorkerService findworkerService;
//
//    @GetMapping("/matched-workers")
//    public ResponseEntity<List<RegUser>> getMatchedWorkers(@RequestParam String skill,
//                                                           @RequestParam String location,
//                                                           @RequestParam(defaultValue = "0") double minRating) {
//        List<RegUser> matchedWorkers = findworkerService.findMatchedWorkers(skill, location, minRating);
//        return ResponseEntity.ok(matchedWorkers);
//    }

}
