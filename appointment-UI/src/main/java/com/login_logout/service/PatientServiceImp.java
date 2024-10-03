package com.login_logout.service;


import com.login_logout.entity.Slot;
import com.login_logout.openfegin.PatientOpenFegin;
import com.login_logout.repo.DoctorTimeSlotRepo;
import com.login_logout.repo.PatientRepo;
import com.login_logout.repo.SlotRepo;
import com.login_logout.response.AppointmentResponse;
import com.login_logout.response.DoctorResponse;
import com.login_logout.response.PatientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

//    @Override
//    public List<Slot> getAvailableSlots(long did) {
//        List<Slot> availableSlots = patientFegin.getAvailableSlots(did);
//        return availableSlots;
//    }


    public List<Slot> getDoctorSlotsForNext7Days(long did) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(7);
        return slotRepo.findSlotsByDoctorAndDateRange(did, today, endDate);

    }


    @Override
    public boolean isSlotAvailable(LocalDate date, LocalTime time, Long did) {
        List<Slot> slots = slotRepo.findBySlotDateAndSlotTimeAndDoctorSlot_Id(date, time, did);
        return slots.isEmpty(); // Returns true if no slots found, meaning it's available
    }

}
