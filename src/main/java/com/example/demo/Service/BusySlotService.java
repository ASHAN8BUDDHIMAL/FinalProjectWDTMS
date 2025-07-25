package com.example.demo.Service;
import com.example.demo.DTO.BusySlotDTO;
import com.example.demo.model.BusySlot;
import com.example.demo.model.CreateTask;
import com.example.demo.model.TaskStatus;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusySlotService {

    @Autowired
    private BusySlotRepo busySlotRepo;


    public BusySlot saveBusySlot(BusySlot busySlot) {
        return busySlotRepo.save(busySlot);
    }

    public List<BusySlot> getBusySlotsByWorker(Long workerId) {
        return busySlotRepo.findByWorkerId(workerId);
    }

    public void deleteBusySlot(Long id) {
        busySlotRepo.deleteById(id);
    }



}
