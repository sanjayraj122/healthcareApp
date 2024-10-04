package com.login_logout.service;


import com.login_logout.entity.Slot;
import com.login_logout.openfegin.PatientOpenFegin;
import com.login_logout.repo.DoctorTimeSlotRepo;
import com.login_logout.repo.PatientRepo;
import com.login_logout.repo.SlotRepo;
import com.login_logout.response.AppointmentResponse;
import com.login_logout.response.DoctorResponse;
import com.login_logout.response.PatientResponse;
import com.login_logout.response.TimeSlotDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PatientServiceImp implements PatientService {

    @Autowired
    private PatientRepo userRepo;

    @Autowired
    PatientOpenFegin patientFegin;

    @Autowired
    SlotRepo slotRepo;

    @Autowired
    DoctorTimeSlotRepo doctorTimeSlotRepo;

    @Override
    public PatientResponse createUser(PatientResponse patient) {

        return patientFegin.createuser(patient);
    }

    @Override
    public boolean checkEmail(String email) {

        return userRepo.existsByEmail(email);
    }

    @Override
    public PatientResponse getByEmail(String email) {

        return patientFegin.getByEmail(email);
    }

    @Override
    public List<AppointmentResponse> getAllAppointmentsByPid(long pid) {

        List<AppointmentResponse> appointmentResponseList = patientFegin.myAppos(pid);
        System.out.println("appointment 2" + appointmentResponseList);
        return appointmentResponseList;
    }

    @Override
    public List<DoctorResponse> getAllDoctors() {

        return patientFegin.getAllDoctors();
    }

    @Override
    public PatientResponse getById(long id) {

        PatientResponse patientResponse = patientFegin.getById(id);

        return patientResponse;
    }

    @Override
    public PatientResponse updatePatient(PatientResponse patient, long id) {

        return patientFegin.Updatepatient(patient, id);
    }

    @Override
    public DoctorResponse getDoctorByDID(long did) {
        // TODO Auto-generated method stub
        return patientFegin.GetDoctorByDid(did);
    }

    @Override
    public AppointmentResponse CreateAppointment(AppointmentResponse appointmentResponse) {

        return patientFegin.CreateAppointment(appointmentResponse);
    }

    @Override
    public AppointmentResponse getAppointmentByAID(long aid) {

        return patientFegin.getAppointmentByAID(aid);
    }

    @Override
    public boolean DeleteAppointment(long aid) {

        return patientFegin.DeleteAppointment(aid);
    }

    @Override
    public boolean DeletePatient(long id) {

        patientFegin.DeletePatient(id);
        return false;
    }

    @Override
    public boolean UpdateAppoStatus(long aid, int st) {
        // TODO Auto-generated method stub
        patientFegin.ChangeStatus(aid, st);
        return true;
    }

    public List<Slot> getDoctorSlotsForNext7Days(long did) {

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(7);

        // Fetch all slots for the doctor within the date range
        List<Slot> slots = slotRepo.findSlotsByDoctorAndDateRange(did, today, endDate);

        // Iterate and update slot status if it is not booked
        return slots;

    }


    // Define all possible time slots for the day
    private final List<String> allTimeSlots = Arrays.asList(
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
            "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"
    );

    @Override
    public List<TimeSlotDTO> getAvailableSlots(long did, LocalDate slotDate) {
        // Fetch all booked slots for the given doctor and date
        List<Slot> bookedSlots = slotRepo.findByIdAndSlotDate(did, slotDate);

        // Convert booked slots to a set of times for easy lookup
        Set<String> bookedTimeSlots = bookedSlots.stream()
                .map(slot -> slot.getSlotTime().toString())
                .collect(Collectors.toSet());

        // Create a list of TimeSlotDTOs with availability information
        List<TimeSlotDTO> availableSlots = new ArrayList<>();
        for (String time : allTimeSlots) {
            boolean isDisabled = bookedTimeSlots.contains(time);
            availableSlots.add(new TimeSlotDTO(time, isDisabled));
        }

        return availableSlots;
    }

}
