package com.example.demo.Controller;
import com.example.demo.Service.BusySlotService;
import com.example.demo.model.BusySlot;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("api/busy")
public class BusySlotController {

    @Autowired
    private BusySlotService busySlotService;

    @PostMapping
    public BusySlot createBusySlot(@RequestBody BusySlot busySlot, HttpSession session) {
        Long workerId = (Long) session.getAttribute("loggedInUserId");
        if (workerId == null) {
            throw new RuntimeException("User is not logged in.");
        }
        busySlot.setWorkerId(workerId);
        return busySlotService.saveBusySlot(busySlot);
    }

    @GetMapping("/my")
    public List<BusySlot> getMyBusySlots(HttpSession session) {
        Long workerId = (Long) session.getAttribute("loggedInUserId");
        if (workerId == null) {
            throw new RuntimeException("User is not logged in.");
        }
        return busySlotService.getBusySlotsByWorker(workerId);
    }

    @DeleteMapping("/{id}")
    public void deleteBusySlot(@PathVariable Long id) {
        busySlotService.deleteBusySlot(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusySlot> updateBusySlot(
            @PathVariable Long id,
            @RequestBody BusySlot updatedBusySlot,
            HttpSession session) {

        Long workerId = (Long) session.getAttribute("loggedInUserId");
        if (workerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            BusySlot result = busySlotService.updateBusySlot(id, workerId, updatedBusySlot);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}