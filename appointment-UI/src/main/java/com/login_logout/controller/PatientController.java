package com.login_logout.controller;

import com.login_logout.config.SlotStatus;
import com.login_logout.entity.DoctorTimeSlot;
import com.login_logout.entity.Slot;
import com.login_logout.repo.DoctorRepo;
import com.login_logout.repo.DoctorTimeSlotRepo;
import com.login_logout.response.AppointmentResponse;
import com.login_logout.response.MedicationResponse;
import com.login_logout.response.PatientResponse;
import com.login_logout.service.MedicationService;
import com.login_logout.service.PatientService;
import com.login_logout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    UserService userService;

    @Autowired
    PatientService patientService;

    @Autowired
    MedicationService medicationService;

    @Autowired
    DoctorRepo doctorRepo;

    @Autowired
    DoctorTimeSlotRepo doctorTimeSlotRepo;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy/MM/dd"), true, 10));
    }

    @ModelAttribute
    private void patientDetails(Model m, Principal p) {
        String email = p.getName();
        PatientResponse patientRespose = patientService.getByEmail(email);

        m.addAttribute("patient", patientRespose);

    }

    @GetMapping("/")
    public String home() {
        return "patient_view/home";
    }

    @GetMapping("/appointments/{pid}")
    public String listOfAppointments(@PathVariable long pid, Model model) {

        List<AppointmentResponse> appos = patientService.getAllAppointmentsByPid(pid);
        appos.sort(Comparator.comparing(AppointmentResponse::getDate).thenComparing(AppointmentResponse::getTime));

        // Retrieve medication details for each appointment
        for (AppointmentResponse appo : appos) {

            MedicationResponse medication = medicationService.getMedicationByAid(appo.getAid());
            if (medication != null) {
                appo.setMedication(medication);
            }

        }

        model.addAttribute("appos", appos);

        return "patient_view/appointments";
    }

    @GetMapping("/doctors")
    public String getAllDoctors(Model model) {
        model.addAttribute("doctors", patientService.getAllDoctors());
        return "patient_view/AllDoctors";
    }

    @GetMapping("/profile/{id}")
    public String getById(@PathVariable long id, Model model) {
        model.addAttribute("patient", patientService.getById(id));
        return "patient_view/profile";
    }

    @GetMapping("/getAllMedication/{pid}")
    public String listOfMedications(@PathVariable long pid, Model model) {
        List<AppointmentResponse> appos = patientService.getAllAppointmentsByPid(pid);
        List<MedicationResponse> medications = new ArrayList<>();

        for (AppointmentResponse appo : appos) {
            List<MedicationResponse> meds = medicationService.getMedicationsByAid(appo.getAid());
            medications.addAll(meds);
        }

        model.addAttribute("medications", medications);
        return "patient_view/medications";
    }

    @PostMapping("/update/{id}")
    public String UpdatePatient(@ModelAttribute PatientResponse patient, @PathVariable long id, Model m, HttpSession session) {
        patient.setId(id);

        PatientResponse patientResponse = patientService.updatePatient(patient, id);

        m.addAttribute("patient", patientResponse);
        session.setAttribute("profile_updated", "Profile updated..");

        return "patient_view/home";
    }

    @GetMapping("/delet/{id}")
    public String DeletePatient(@PathVariable long id) {
        if (patientService.DeletePatient(id)) {
            return "index";
        }
        return "patient_view/home";
    }

    @GetMapping("/book/{did}")
    public String GetAppointmentPage(@PathVariable long did, Model model) {
        model.addAttribute("doctor", patientService.getDoctorByDID(did));
        model.addAttribute("medications", medicationService.getAllMedicationsByDid(did));

        return "patient_view/bookAppointment";
    }

    @PostMapping("/book/{id}")
    public String CreateAppointment(@ModelAttribute("appointment") AppointmentResponse appointment, @PathVariable long id, HttpSession session) {

        appointment.setPid(id);

        appointment.setStatus("Pending");

        // Get the existing appointments for the patient
        List<AppointmentResponse> existingAppointments = patientService.getAllAppointmentsByPid(id);

        // Check if the appointment time is already booked for the patient within 30 minutes
        if (isAppointmentTimeAvailable(existingAppointments, appointment)) {
            AppointmentResponse response = patientService.CreateAppointment(appointment);

            DoctorTimeSlot doctorTimeSlot = new DoctorTimeSlot();
            List<Slot> slotList = new ArrayList<>();
            Slot slot = new Slot();
            slot.setSlotDate(appointment.getDate());
            slot.setSlotTime(appointment.getTime());
            slot.setStatus(String.valueOf(SlotStatus.BOOKED));
            slot.setAid(response.getAid());
            slotList.add(slot);
            slot.setDoctorSlot(doctorTimeSlot);

            doctorTimeSlot.setDoctor(doctorRepo.findById(appointment.getDid()).get());
            doctorTimeSlot.setSlots((slotList));
            // Save the Slot object
            doctorTimeSlotRepo.save(doctorTimeSlot);

            session.setAttribute("booked", "Appointment Booked Successfully.");
            return "redirect:/patient/appointments/" + appointment.getPid();
        } else {
            session.setAttribute("error", "Appointment time is already booked for patient within 30 minutes. Please choose a different time.");
            return "redirect:/patient/appointments/" + id;

        }
    }

    private boolean isAppointmentTimeAvailable(List<AppointmentResponse> existingAppointments, AppointmentResponse newAppointment) {
        for (AppointmentResponse existingAppointment : existingAppointments) {
            if (existingAppointment.getDate().equals(newAppointment.getDate())) {
                long minutesDiff = ChronoUnit.MINUTES.between(existingAppointment.getTime(), newAppointment.getTime());
                if (minutesDiff >= -29 && minutesDiff <= 29) {
                    return false;

                }
            }
        }

        return true;
    }

    @GetMapping("/editAppo/{aid}")
    public String EditAppointment(@PathVariable long aid, Model model) {
        AppointmentResponse appointmentResponse = patientService.getAppointmentByAID(aid);
        model.addAttribute("appointment", appointmentResponse);

        // Fetch doctor ID (did) from the appointment response
        long did = appointmentResponse.getDid();

        // Fetch slots for the next 7 days for this doctor
        List<Slot> slotsForNext7Days = patientService.getDoctorSlotsForNext7Days(did);

        // Sort the slots by date and time in ascending order
        slotsForNext7Days.sort(Comparator.comparing(Slot::getSlotDate).thenComparing(Slot::getSlotTime));

        // Add slots and other attributes to the model
        model.addAttribute("slotsForNext7Days", slotsForNext7Days);
        model.addAttribute("doctorId", did);

        return "patient_view/availability";
    }

    @PostMapping("/updateApp/{id}/{did}")
    public String UpdateAppointment(@ModelAttribute("appointment") AppointmentResponse appointment, @PathVariable("id") long id, @PathVariable("did") long did, HttpSession session) {
        appointment.setPid(id);
        appointment.setDid(did);
        appointment.setStatus("Pending");

        List<AppointmentResponse> appos = patientService.getAllAppointmentsByPid(id);

        if (isAppointmentTimeAvailable(appos, appointment)) {
            CreateAppointment(appointment, id, session);
            session.setAttribute("booked", "Appointment Updated.");
            return "redirect:/patient/appointments/" + appointment.getPid() + "?success=true";
        } else {
            session.setAttribute("error", "Appointment time is already booked . Please choose a different time.");
            return "redirect:/patient/appointments/" + id;
        }
    }

    @GetMapping("/deleteAppo/{aid}")
    public String DeleteAppointment(@PathVariable long aid, HttpSession session) {
        AppointmentResponse appointmentResponse = patientService.getAppointmentByAID(aid);
        patientService.DeleteAppointment(aid);
        session.setAttribute("deleted", "Appointment deleted");
        return "redirect:/patient/appointments/" + appointmentResponse.getPid();
    }

    @GetMapping("/status/{aid}/{st}")
    public String ChangeStatusAppo(@PathVariable long aid, @PathVariable int st) {
        patientService.UpdateAppoStatus(aid, st);
        return "patient_view/home";
    }

    @GetMapping("/availability/{did}")
    public String getDoctorAvailabilityForNext7Days(@ModelAttribute("appointment") AppointmentResponse appointment,@PathVariable long did, Model model) {

        List<Slot> slotsForNext7Days = patientService.getDoctorSlotsForNext7Days(did);
        // Sort slots by date and time in ascending order
        slotsForNext7Days.sort(Comparator.comparing(Slot::getSlotDate).thenComparing(Slot::getSlotTime));
        model.addAttribute("slotsForNext7Days", slotsForNext7Days);

        return "patient_view/availability";
    }

}
