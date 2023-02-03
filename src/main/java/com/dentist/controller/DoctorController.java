package com.dentist.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.dentist.entity.AppointmentEntity;
import com.dentist.entity.DoctorEntity;
import com.dentist.pojo.Login;
import com.dentist.pojo.Result;

import com.dentist.service.DoctorService;
import com.dentist.utility.TokenUtility;

@RestController
@RequestMapping("/Doctor")
public class DoctorController {
	@Autowired
	private DoctorService service;
	@Autowired
	TokenUtility tolenUtility;

	// login
	@PostMapping("/login")
	public Result login(@RequestBody @Valid Login login) {

		return service.login(login);
	}

	// register for Doctor
	@PostMapping("/Register")
	public Result addDoctor(@RequestBody DoctorEntity doctor) {

		return service.addDoctorProfile(doctor);
	}

	// update Doctor
	@PutMapping("/updateDoctor")
	public Result updateDoctor(HttpServletRequest request, HttpServletResponse response,
			@RequestBody DoctorEntity doctor) {
		Result result = tolenUtility.checkToken(request.getHeader("token"));
		if (result.getStatus() == 0) {
			return service.Updatedoctor(doctor);
		} else {
			return result;
		}
	}

	// create appointment
	@PostMapping("/createAppointment")
	public Result createAppointment(HttpServletRequest request, HttpServletResponse response,
			@RequestBody AppointmentEntity appEntity) {
		Result result = tolenUtility.checkToken(request.getHeader("token"));
		if (result.getStatus() == 0) {
			return service.createAppointment(appEntity);
		} else {
			return result;
		}
	}

	// to delete appointment.
	@DeleteMapping("/deleteAppointment")
	public Result deleteAppointment(HttpServletRequest request, HttpServletResponse response, @RequestParam int id) {
		Result result = tolenUtility.checkToken(request.getHeader("token"));
		if (result.getStatus() == 0) {
			return service.deleateAppointment(id);

		} else {
			return result;
		}
	}

	// to change the patient visit the doctor or not .
	@PutMapping("/changeIsVisited")
	public Result DoctorchangeIsVisited(HttpServletRequest request, HttpServletResponse response,
			@RequestParam int status ,@RequestParam int appointmentId  ) {
		Result result = tolenUtility.checkToken(request.getHeader("token"));
		if (result.getStatus() == 0) {
			return service.changeIsVisited(status,appointmentId);

		} else {
			return result;
		}
	}

	// to get all booked time for doctor on the current day
	@GetMapping("/allbooktime")
	public Result allbook(HttpServletRequest request, HttpServletResponse response, @RequestParam int id) {
		Result result = tolenUtility.checkToken(request.getHeader("token"));

		if (result.getStatus() == 0) {
			return service.allbookedtime(id);
		} else {
			return result;
		}

	}

	// to get all Available time for doctor on the current day
	@GetMapping("/allTimeAvailable")
	public Result allTime(HttpServletRequest request, HttpServletResponse response, @RequestParam int id) {
		Result result = tolenUtility.checkToken(request.getHeader("token"));
		if (result.getStatus() == 0) {
			return service.allTimeAvailable(id);
		} else {
			return result;
		}
	}

	// The doctor can see a list of all patients.
	@GetMapping("/AllPatient")
	public Result listofAllpatient(HttpServletRequest request, HttpServletResponse response) {
		Result result = tolenUtility.checkToken(request.getHeader("token"));
		if (result.getStatus() == 0) {
			return service.listofAllPatient();
		} else {
			return result;
		}
	}

	// The Doctor Can visit the Patient Profile (See information Patient username,
	// phone number, …etc.).
	@GetMapping("/visitPatientProfile")
	public Result visitPatient(HttpServletRequest request, HttpServletResponse response, @RequestParam int id) {
		Result result = tolenUtility.checkToken(request.getHeader("token"));
		if (result.getStatus() == 0) {
			return service.findPatient(id);
		} else {
			return result;
		}
	}

	// The patient visited count: check how many the patient visited the doctor.
	@GetMapping("/patientVisitCount")
	public Result countPatient(HttpServletRequest request, HttpServletResponse response, @RequestParam int doctorid,
			@RequestParam int patientid) {
		Result result = tolenUtility.checkToken(request.getHeader("token"));
		if (result.getStatus() == 0) {
			return service.countpatient(doctorid, patientid);
		} else {
			return result;
		}
	}

	@GetMapping("/datebetween")
	public Result between(HttpServletRequest request, HttpServletResponse response,
			@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate start,
			@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate end) {
		Result result = tolenUtility.checkToken(request.getHeader("token"));
		if (result.getStatus() == 0) {
			return service.datebetween(start, end);
		} else {
			return result;
		}
	}

	@GetMapping(value = "/doctorrport", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public Object dawanloadrepo(HttpServletRequest request, HttpServletResponse response,
			@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate start,
			@DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate end, @RequestParam int doctorid) {
		String filepathe = service.createCsvFile(start, end, doctorid);
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

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Result handleValidationExceptions(MethodArgumentNotValidException ex) {
		Result result = new Result();
		result.setStatus(1);

		Map<String, Object> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		result.setResultMap(errors);
		return result;

	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public Result handleAllExceptionMethod(Exception ex, WebRequest requset, HttpServletResponse res) {
		Result result = new Result();
		result.setStatus(1);

		Map<String, Object> errors = new HashMap<>();
		errors.put("Exception", ex.getCause());
		result.setResultMap(errors);
		return result;
	}

}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

//	@PutMapping("/updateDoctorPhoneNumber")
//	public Result updateDoctorPhoneNumber(HttpServletRequest request, HttpServletResponse response,
//			@RequestBody DoctorEntity doctor) {
//		Result result = tolenUtility.checkToken(request.getHeader("token"));
//		if (result.getStatus() == 0) {
//			return service.updateDoctorSetPhoneNumberWhereId(doctor);
//		} else {
//			return result;
//		}
//	}

//	@PutMapping("/findAllavalabliTimeDoctor")
//	public List<AppointmentEntity> AppointmentEntity(HttpServletRequest request, HttpServletResponse response,
//			@RequestBody AppointmentEntity appointment) {
//		Result result = tolenUtility.checkToken(request.getHeader("token"));
//		if (result.getStatus() == 0) {
//			return service.findAllavalabli();
//		} else {
//			return null;
//		}
//	}

//	@GetMapping("/findPateintById")
//	public Result findPateintById(HttpServletRequest request, HttpServletResponse response, @RequestBody int id) {
//		Result result = tolenUtility.checkToken(request.getHeader("token"));
//		if (result.getStatus() == 0) {
//			return service.findPateintById(id);
//		} else {
//			return result;
//		}
//	}

//	@PutMapping("/updateDoctorPassword")
//	public Result updateDoctorPasswordr(HttpServletRequest request, HttpServletResponse response,
//			@RequestBody DoctorEntity doctor) {
//		Result result = tolenUtility.checkToken(request.getHeader("token"));
//		if (result.getStatus() == 0) {
//			return service.updateDoctorSetPasswordWhereId(doctor);
//		} else {
//			return result;
//		}
//	}