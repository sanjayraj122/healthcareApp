package com.login_logout.service;


import com.login_logout.entity.Slot;
import com.login_logout.response.AppointmentResponse;
import com.login_logout.response.DoctorResponse;
import com.login_logout.response.PatientResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PatientService {

    PatientResponse createUser(PatientResponse patient);

    boolean checkEmail(String email);

    PatientResponse getByEmail(String email);

    List<AppointmentResponse> getAllAppointmentsByPid(long pid);


    List<DoctorResponse> getAllDoctors();

    PatientResponse getById(long id);

    PatientResponse updatePatient(PatientResponse patient, long id);

    DoctorResponse getDoctorByDID(long did);

    AppointmentResponse CreateAppointment(AppointmentResponse appointmentResponse);

    AppointmentResponse getAppointmentByAID(long aid);

    boolean DeleteAppointment(long aid);

    boolean DeletePatient(long did);

    boolean UpdateAppoStatus(long aid, int st);

//    List<Slot> getAvailableSlots(long did);


    List<Slot> getDoctorSlotsForNext7Days(long did);

    boolean isSlotAvailable(LocalDate date, LocalTime time, Long did);

}
