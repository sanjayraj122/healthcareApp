package com.login_logout.repo;


import com.login_logout.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SlotRepo extends JpaRepository<Slot, Long> {
    List<Slot> findSlotsByDoctorSlot_Doctor_DidAndSlotDate(long did, LocalDate slotDate);

    @Query("SELECT s FROM Slot s WHERE s.doctorSlot.doctor.did = :did AND s.slotDate BETWEEN :startDate AND :endDate")
    List<Slot> findSlotsByDoctorAndDateRange(long did, LocalDate startDate, LocalDate endDate);

    List<Slot> findByIdAndSlotDate(long did, LocalDate slotDate);


}
