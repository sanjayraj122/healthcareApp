package com.login_logout.repo;


import com.login_logout.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SlotRepo extends JpaRepository<Slot, Long> {
    List<Slot> findSlotsByDoctorSlot_Doctor_DidAndSlotDate(long did, LocalDate slotDate);

    @Query("SELECT s FROM Slot s WHERE s.doctorSlot.doctor.did = :did AND s.slotDate BETWEEN :startDate AND :endDate")
    List<Slot> findSlotsByDoctorAndDateRange(long did, LocalDate startDate, LocalDate endDate);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Slot s WHERE s.doctorSlot.doctor.did = :did AND s.slotDate = :date AND s.slotTime = :time AND s.status = 'EMPTY'")
    boolean checkSlotAvailability(long did, LocalDate date, LocalTime time);

    List<Slot> findBydoctorSlotAndSlotDateBetweenAndSlotTimeAndStatus(Long did, LocalDate startDate, LocalDate endDate, LocalTime time, String status);

    List<Slot> findBySlotDateAndSlotTimeAndDoctorSlot_Id(LocalDate date, LocalTime time, Long did);

}
