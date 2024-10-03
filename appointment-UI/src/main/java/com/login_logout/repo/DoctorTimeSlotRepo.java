package com.login_logout.repo;


import com.login_logout.entity.DoctorTimeSlot;
import com.login_logout.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DoctorTimeSlotRepo extends JpaRepository<DoctorTimeSlot,Long> {

        Optional<DoctorTimeSlot> findById(long did);

        @Query("SELECT s FROM Slot s WHERE s.doctorSlot.doctor.did = :did AND s.slotDate = :date")
        List<Slot> findSlotsByDidAndDate(@Param("did") long did, @Param("date") LocalDate date);
}


