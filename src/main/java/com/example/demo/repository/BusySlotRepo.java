package com.example.demo.repository;
import com.example.demo.model.BusySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BusySlotRepo extends JpaRepository<BusySlot, Long> {
    @Query("SELECT b FROM BusySlot b WHERE b.workerId = :workerId AND b.date = :date")
    List<BusySlot> findByWorkerIdAndDate(@Param("workerId") Long workerId, @Param("date") LocalDate date);

    // Optionally keep your existing method if needed
    List<BusySlot> findByWorkerId(Long workerId);
    Optional<BusySlot> findByIdAndWorkerId(Long id, Long workerId);

}
