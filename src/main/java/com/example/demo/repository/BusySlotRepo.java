package com.example.demo.repository;
import com.example.demo.model.BusySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BusySlotRepo extends JpaRepository<BusySlot, Long> {
    List<BusySlot> findByWorkerId(Long workerId);
}
