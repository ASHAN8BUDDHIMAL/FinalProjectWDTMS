package com.example.demo.Service;
import com.example.demo.model.BusySlot;
import com.example.demo.repository.BusySlotRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

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
