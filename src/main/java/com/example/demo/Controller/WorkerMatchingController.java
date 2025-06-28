package com.example.demo.Controller;

import com.example.demo.DTO.MatchedWorkerDTO;
import com.example.demo.Service.WorkerMatchingService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class WorkerMatchingController {

    @Autowired
    private final WorkerMatchingService workerMatchingService;

    @GetMapping("/workers/{taskId}")
    public List<MatchedWorkerDTO> getMatchedWorkers(@PathVariable Long taskId) {
        return workerMatchingService.findMatchedWorkers(taskId);
    }

}
