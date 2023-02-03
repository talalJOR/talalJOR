package com.dentist.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dentist.entity.AppointmentEntity;

import com.dentist.entity.PatientEntity;
import com.dentist.pojo.Login;
import com.dentist.pojo.Result;
import com.dentist.service.PatientService;
import com.dentist.utility.TokenUtility;

@RestController
@RequestMapping("/patient")
public class PatientController {
	@Autowired
	PatientService service;
	@Autowired
	TokenUtility tokenUtility;

	// patient login.
	@PostMapping("/login")
	public Result login(@RequestBody @Valid Login login) {

		return service.login(login);
	}

	// patient register.
	@PostMapping("/Register")
	public Result addpatient(HttpServletRequest request, HttpServletResponse response,
			@RequestBody PatientEntity patient) {

		return service.addPatient(patient);

	}

	// The patient can update with their password, name, phone number, age, and
	// gender.
	@PutMapping("/updatepatient")
	public Result updatePatient(HttpServletRequest request, HttpServletResponse response,
			@RequestBody PatientEntity patient) {
		Result result = tokenUtility.checkToken(request.getHeader("token"));
		if (result.getStatus() == 0) {
			return service.UpdatePatientProfile(patient);
		} else {
			return result;
		}
	}

	// The Patient Can Create Appointment.
	@PostMapping("/createAppointment")
	public Result createAppointment(HttpServletRequest request, HttpServletResponse response,
			@RequestBody AppointmentEntity appEntity) {
		Result result = tokenUtility.checkToken(request.getHeader("token"));
		if (result.getStatus() == 0) {
			return service.createAppointment(appEntity);
		} else {
			return result;
		}
	}
	
	
	// ❖ The Patient can see a list of all Doctors.
	@GetMapping("/AllDoctors")
	public Result listofAllDoctor(HttpServletRequest request, HttpServletResponse response) {
		Result result = tokenUtility.checkToken(request.getHeader("token"));
		if (result.getStatus() == 0) {
			return service.listofAlldoctor();
		} else {
			return result;
		}
	}

	// The Patient Can Show a Report To Check All Timelines in the Dentist.
	@GetMapping(value = "/patientrport", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public Object repopatient(HttpServletRequest request, HttpServletResponse response,
			@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate start,
			@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate end, @RequestParam int patientid) {
		String filepathe = service.createCsvFile(start, end, patientid);
		InputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(new File(filepathe)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStreamResource resource = new InputStreamResource(input);

		return resource;
	}
   //cancel appointment 
	@DeleteMapping("/deletePatientAppoitment")
	public Result deletePatientAppoitment(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int patientId) {
		Result result = tokenUtility.checkToken1(request.getHeader("token"));
		if (result.getStatus() == 0) {
			return service.deleateAppointment(patientId);
		} else {
			return result;
		}
	}



	// to get all Available time for doctor on the current day
		@GetMapping("/allDoctorAvailable")
		public Result doctorsAvailable(HttpServletRequest request, HttpServletResponse response, 	@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date,
				@DateTimeFormat(pattern = "HH:mm") LocalTime time) {
			Result result = tokenUtility.checkToken(request.getHeader("token"));
			if (result.getStatus() == 0) {
				return service.allTimeAvailable(date,time);
			} else {
				return result;
			}
		}
    //update patient
	@PutMapping("/UpdatePatient")
	public Result UpdatePatient(HttpServletRequest request, HttpServletResponse response,
			@RequestBody PatientEntity patient) {
		Result result = tokenUtility.checkToken(request.getHeader("token"));
		if (result.getStatus() == 0) {
			return service.UpdatePatientProfile(patient);
		} else {
			return result;
		}
	}
//	@GetMapping("/findalldoctor")
//	public List<DoctorEntity> findalldoctors(HttpServletRequest request, HttpServletResponse response) {
//
//		Result result = tokenUtility.checkToken(request.getHeader("token"));
//		if (result.getStatus() == 0) {
//			return service.findalldoctor();
//		} else {
//			return null;
//		}
//	}
//	@PutMapping("/UpdatePatientUsername")
//	public Result UpdatePatientUsername(HttpServletRequest request, HttpServletResponse response,
//			@RequestBody PatientEntity patient) {
//		Result result = tokenUtility.checkToken(request.getHeader("token"));
//		if (result.getStatus() == 0) {
//			return service.updatePatientSetUserNameWhereId(patient);
//		} else {
//			return result;
//		}
//	}

}
