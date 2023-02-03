package com.dentist.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dentist.entity.AppointmentEntity;
import com.dentist.entity.DoctorEntity;
import com.dentist.entity.PatientEntity;
import com.dentist.pojo.Login;
import com.dentist.pojo.Result;

import com.dentist.repository.Appointment;
import com.dentist.repository.DoctorRepo;
import com.dentist.repository.PatientRepo;
import com.dentist.utility.TokenUtility;

@Service
public class DoctorService {
	@Autowired
	PatientRepo patientRepo;
	@Autowired
	DoctorRepo doctorrepo;
	@Autowired
	Appointment appointmentrepo;
	@Autowired
	TokenUtility tokenUtility;
//	private static Map<LocalDate, List<AppointmentEntity>> appointments = new HashMap<>();
	private static final LocalTime START_TIME = LocalTime.of(8, 0);
	private static final LocalTime END_TIME = LocalTime.of(17, 0);

	// login
	public Result login(Login login) {
		Result result = new Result();
		Map<String, Object> mapResult = new HashMap<>();
		DoctorEntity doctor = doctorrepo.findDoctorByUserName(login.username);
		if (doctor == null) {
			result.setStatus(1);
			result.setStatusDescription("doctor Not Found ");
			return result;
		}
		if (!doctor.getPassword().equalsIgnoreCase(login.getPassword())) {

			result.setStatusDescription("Inncorect Password");
			result.setStatus(1);
			return result;

		}
		String token = tokenUtility.generateToken(login.getUsername());
		mapResult.put("doctor token", token);
		result.setStatus(0);
		result.setResultMap(mapResult);
		return result;
	}

	// create appointment
	public Result createAppointment(AppointmentEntity appointment1) {
		Result result = new Result();
		Map<String, Object> mapResult = new HashMap<>();
		LocalDate ld = LocalDate.now();
		List<AppointmentEntity> appointmentEntity2 = appointmentrepo.findByDateAndTime(appointment1.getDate(),
				appointment1.getTime().minusMinutes(appointment1.getTime().getMinute()));

		if (appointment1.getTime().isBefore(START_TIME) || appointment1.getTime().isAfter(END_TIME)) {

			result.setStatus(1);

			result.setStatusDescription("The appointment is outside of the dentist's working hours.");
			return result;
		}

		if (appointment1.getDate().isBefore(ld)) {

			result.setStatus(1);

			result.setStatusDescription("you cannt create Appointment before The date of this day ");
			return result;
		}
		Optional<DoctorEntity> chickdoctor = doctorrepo.findById(appointment1.getDoctor().getId());
		if (chickdoctor.isEmpty()) {
			result.setStatus(1);

			result.setStatusDescription("no doctor found  ");
			return result;
		}
		Optional<PatientEntity> chickpatient = patientRepo.findById(appointment1.getPatient().getId());
		if (chickpatient.isEmpty()) {
			result.setStatus(1);

			result.setStatusDescription("no patient found ");
			return result;
		}
		for (AppointmentEntity appointmentn : appointmentEntity2) {
			if (appointmentn.getDoctor().getId() == appointment1.getDoctor().getId()
					&& appointmentn.getDate().isEqual(appointment1.getDate())
					&& appointmentn.getTime().equals(appointment1.getTime().minusMinutes(appointment1.getTime().getMinute()))) {
				result.setStatus(1);
				result.setStatusDescription("The appointment overlaps with another appointment.");
				return result;
			}

		}
		appointment1.setVisited(false);
		appointment1.setBooked(true);
		appointment1.setTime(appointment1.getTime().minusMinutes(appointment1.getTime().getMinute()));
		appointmentrepo.save(appointment1);
		result.setStatus(0);
		result.setResultMap(mapResult);
		result.setStatusDescription("Appointment created successfully.");
		return result;
	}

	// cancel appointment
	public Result deleateAppointment(int id) {
		Result result = new Result();
		Map<String, Object> mapResult = new HashMap<>();

		if (id < 0) {

			result.setStatus(1);
			result.setStatusDescription("Cannot Send Id negative");

			return result;
		}
		if (appointmentrepo.findById(id) == null) {

			result.setStatus(1);
			result.setStatusDescription("no Appointment found");
			return result;

		}
		appointmentrepo.deleteById(id);
		result.setStatus(0);
		result.setStatusDescription("Delete successful.");
		result.setResultMap(mapResult);
		return result;
	}

	// add Doctor/ Register
	public Result addDoctorProfile(DoctorEntity doctor1) {
		Result result = new Result();
		Map<String, Object> mapResult = new HashMap<>();
		DoctorEntity doctor = doctor1;

		if (doctor.getUserName() == null || doctor.getUserName().isEmpty()) {
			result.setStatus(1);
			mapResult.put("DoctorEntity ", "Cannot Send DoctorEntity  Empty");
			result.setResultMap(mapResult);
			return result;
		}
		if (doctor.getUserName() == null || doctor.getUserName().isEmpty()) {
			result.setStatus(1);
			mapResult.put("DoctorEntity ", "Cannot Send DoctorEntity  Empty");
			result.setResultMap(mapResult);
			return result;
		}
		if (doctor.getId() != null) {
			if (doctor.getId() < 0) {
				result.setStatus(1);
				mapResult.put("Id", "Cannot Send Id negative");
				result.setResultMap(mapResult);
				return result;
			}
		}
        DoctorEntity finddoc = doctorrepo.findDoctorByUserName(doctor1.getUserName());
        if (finddoc!=null) {
        if (finddoc.getUserName().equalsIgnoreCase(doctor.getUserName())) {
			result.setStatus(1);
			mapResult.put("user name  ", "user name is used");
			result.setResultMap(mapResult);
			return result;
		}}
		doctorrepo.save(doctor);
		result.setStatus(0);
		result.setStatusDescription("add  Sccessful.");
		result.setResultMap(mapResult);
		return result;

	}

	// The Doctor Can See Booked Timeline: the doctor can see all Booked time on
	// current day from 08:00 AM to 17:00PM:
	public Result allbookedtime(int id) {
		Result result = new Result();
		Map<String, Object> mapResult = new HashMap<>();
		LocalDate date = LocalDate.now();
		List<AppointmentEntity> list = appointmentrepo.findByDoctorIdAndDate(id, date);
		if (id < 0) {

			result.setStatus(1);
			result.setStatusDescription("Cannot Send Id negative.");
			mapResult.put("Id", "Cannot Send Id negative.");
			result.setResultMap(mapResult);
			return result;
		}
		if (list.isEmpty()) {

			result.setStatus(1);
			result.setStatusDescription("No booked time.");
			result.setResultMap(mapResult);
			return result;
		}

		List<String> allbooked1 = list.stream()
				.map(AppointmentEntity -> " patient : " + AppointmentEntity.getPatient().getName() + " Time : "
						+ AppointmentEntity.getTime() + " patientid : " + AppointmentEntity.getPatient().getId())
				.collect(Collectors.toList());
		result.setStatus(0);
		result.setStatusDescription("Successful.");
		mapResult.put("All Book Time :", allbooked1);
		result.setResultMap(mapResult);
		return result;
	}

//   for(AppointmentEntity appointment:apo)
//   {
//	   appointment.setDoctor(null);  
//	   appointment.setDate(null);
//	   appointment.setDate(null);
//	   appointment.getPatient().setUserName(null);
//	   appointment.getPatient().setPassword(null);
//	   appointment.getPatient().setPhoneNumber(null);
//	   appointment.getPatient().setGender(null);
//	   appointment.getPatient().setAge(0);
//	   
//   } 

//  	Stream<String> time= Stream.of("08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00");
	public Result allTimeAvailable(int id) {
		Result result = new Result();
		Map<String, Object> mapResult = new HashMap<>();

		List<String> time = new ArrayList<String>();
		time.add("08:00");
		time.add("09:00");
		time.add("10:00");
		time.add("11:00");
		time.add("12:00");
		time.add("13:00");
		time.add("14:00");
		time.add("15:00");
		time.add("16:00");
		time.add("17:00");
		LocalDate ld = LocalDate.now();
		List<AppointmentEntity> listofappAppointmentEntities = appointmentrepo.findByDoctorIdAndDate(id, ld);
		if (id < 0) {

			result.setStatus(1);
			result.setStatusDescription("Cannot Send Id negative.");
			mapResult.put("Id", "Cannot Send Id negative.");
			result.setResultMap(mapResult);
			return result;

		}
		if (listofappAppointmentEntities.isEmpty()) {

			result.setStatus(1);
			result.setStatusDescription("no  appointment");
			mapResult.put("Doctor", "No  appointment in this doctor id and date.");
			result.setResultMap(mapResult);
			return result;

		}
		List<String> allbooked = listofappAppointmentEntities.stream()
				.filter(AppointmentEntity -> AppointmentEntity.getTime().isAfter(START_TIME))
				.map((AppointmentEntity) -> AppointmentEntity.getTime().toString()).collect(Collectors.toList());
		time.removeAll(allbooked);
		if (time.isEmpty()) {
			result.setStatus(1);
			result.setStatusDescription("All Time booked");
			mapResult.put("Doctor", "No  Available Time today.");
			result.setResultMap(mapResult);
			return result;
		}
		result.setStatus(0);
		result.setStatusDescription("successful");
		mapResult.put("Available time :", time);
		result.setResultMap(mapResult);
		return result;

	}

	// The doctor can see a list of all patients.
	public Result listofAllPatient() {
		Result result = new Result();
		Map<String, Object> mapResult = new HashMap<>();
		List<PatientEntity> listpatient = new ArrayList<PatientEntity>();
		listpatient = patientRepo.findAll();
		List<String> liststring = listpatient.stream().map(PatientEntity -> "Name:" + PatientEntity.getName())
				.collect(Collectors.toList());

		if (liststring.isEmpty()) {

			result.setStatus(1);
			result.setStatusDescription("no  pateint");
			result.setResultMap(mapResult);
			return result;
		}

		result.setStatus(0);
		result.setStatusDescription("successful");
		mapResult.put("List of all patient : ", liststring);
		result.setResultMap(mapResult);
		return result;
	}

	// The Doctor Can visit the Patient Profile (See information Patient username,
	// phone number, …etc.).
	public Result findPatient(int id) {

		Result result = new Result();
		Map<String, Object> mapResult = new HashMap<>();
		PatientEntity patient = patientRepo.findPatientById(id);

		if (id < 0) {

			result.setStatus(1);
			result.setStatusDescription("Id less than 0 .");
			result.setResultMap(mapResult);
			return result;

		}
		if (patient == null) {

			result.setStatus(1);
			result.setStatusDescription("No patient .");
			result.setResultMap(mapResult);
			return result;

		}
		patient.setPassword(null);
		result.setStatus(0);
		mapResult.put("patient", patient);
		result.setResultMap(mapResult);
		return result;

	}

	// Update Doctor
	public Result Updatedoctor(DoctorEntity doctor) {
		Result result = new Result();
		Map<String, Object> mapResult = new HashMap<>();
		DoctorEntity doctor1 = doctorrepo.findDoctorByUserName(doctor.getUserName());
		if (doctor1.getUserName() == null || doctor1.getUserName().isEmpty()) {
			result.setStatus(1);
			mapResult.put("DoctorEntity ", "Cannot Send DoctorEntity Empty.");
			result.setResultMap(mapResult);
			return result;
		}
		if (doctor1.getId() != null) {
			if (doctor1.getId() < 0) {
				result.setStatus(1);
				mapResult.put("Id", "Cannot Send Id negative.");
				result.setResultMap(mapResult);
				return result;
			}
		}
		doctorrepo.save(doctor);
		result.setStatus(0);
		result.setStatusDescription("Successful.");
		result.setResultMap(mapResult);
		return result;
	}

	// to change the patient visit the doctor or not
	public Result changeIsVisited(int status, int appointmentId) {
		Result result = new Result();

		Optional<AppointmentEntity> appointmen1 = appointmentrepo.findById(appointmentId);
		if (appointmentId<0) {
			result.setStatus(1);
			result.setStatusDescription("id sent not valied .");
			return result;
		}
		if(appointmen1.isEmpty()) {
			
			result.setStatus(1);
			result.setStatusDescription("no appoitment" );
			return result;	
		}

		if ( status==1) {
			appointmen1.get().setVisited(true);
			appointmentrepo.save(appointmen1.get());
			result.setStatus(0);
			result.setStatusDescription("visit update ture");
			result.setStatusDescription("successful");
			return result;

		}
		if ( status==0) {

			result.setStatus(1);
			result.setStatusDescription("Not visit.");
			return result;
		}
		result.setStatus(1);
		result.setStatusDescription("Send status 0 or 1 .");
		return result;

	}

	// count //the patient visited count: check how many the patient visited the
	// doctor.
	public Result countpatient(int doctorid, int patientid) {
		Result result = new Result();
		Map<String, Object> mapResult = new HashMap<>();
		if (doctorid < 0 || patientid < 0) {
			result.setStatus(1);
			result.setStatusDescription("Doctor id or patient id Cannot Send Id negative.");
			return result;
		}
		int count = 0;
		DoctorEntity doctor = doctorrepo.findDoctorById(doctorid);
		PatientEntity patient = patientRepo.findPatientById(patientid);
		if (doctor == null || patient == null) {
			result.setStatus(1);
			result.setStatusDescription("Doctor  or patient not found.");
			return result;
		}

		List<AppointmentEntity> appointmentlist = appointmentrepo.findAppointmentByDoctorId(doctorid);
		for (AppointmentEntity appointmentn : appointmentlist) {
			if (appointmentn.getDoctor().getId() == doctor.getId()
					&& appointmentn.getPatient().getId() == patient.getId()) {
				count++;
			}
		}
		result.setStatus(0);
		result.setStatusDescription("visit update ture");
		mapResult.put("patient visit count = ", count);
		result.setResultMap(mapResult);
		return result;
	}

	// ❖: doctor can get the summary for all booked timelines from -to date
	public Result datebetween(LocalDate start, LocalDate end) {
		Result result = new Result();
		Map<String, Object> mapResult = new HashMap<>();

		List<AppointmentEntity> datebetweentwodate = appointmentrepo.findByDateBetween(start, end);

		if (datebetweentwodate.isEmpty()) {
			result.setStatus(1);
			result.setStatusDescription("No appointment btween this two date .");
			return result;
		}

		result.setStatus(0);
		result.setStatusDescription("Successful.");
		mapResult.put("Appointment ", datebetweentwodate);
		result.setResultMap(mapResult);
		return result;
	}

	// Doctor Reports: doctor can get the summary for all booked timelines from -to
	// date
	public String createCsvFile(LocalDate start, LocalDate end, int id) {
		List<AppointmentEntity> allAppointmentbtween = appointmentrepo.findByDateBetween(start, end);
		File file = new File("C:\\Users\\talal\\Desktop\\repo\\doctor.csv");
		try (PrintWriter writer = new PrintWriter(file);) {
			writer.println(" DoctorName , PatientName , Date , Time");
			allAppointmentbtween.forEach(x -> {
				if (x.getDoctor().getId() == id) {
					writer.println(x.getDoctor().getDname() + "," + x.getPatient().getName() + ","
							+ x.getDate().toString() + "," + x.getTime());
				}
			});
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	public int countpatient(int doctorid,int patientid) {
//		Result result = new Result(); 
//	
//		List<AppointmentEntity> appointmens= appointmentrepo.findByDoctorIdAndpatientId(doctorid, patientid);
//		
//		if (appointmens.isEmpty()) {
//			result.setStatus(1);
//			result.setStatusDescription("no appointment in this doctorid or patientid");
//			return 0;
//		}
//		List<String>a=appointmens.stream()	
//    			.map((AppointmentEntity)-> AppointmentEntity.getPatient().getName()
//    		       ).collect(Collectors.toList());
//		Integer count =(int) a.stream().count();
//		result.setStatus(0);
//
//		return count;	
//	
//	}

//	public Result updateDoctorSetUserNameWhereId(DoctorEntity doctor) {
//		Result result = new Result();
//		Map<String, Object> mapResult = new HashMap<>();
//		if (doctor.getUserName() == null || doctor.getUserName().isEmpty()) {
//			result.setStatus(1);
//			mapResult.put("NameEntity", "Cannot Send Name Entity Empty");
//			result.setResultMap(mapResult);
//			return result;
//		}
//		if (doctor.getId() != null) {
//			if (doctor.getId() < 0)
//				result.setStatus(1);
//			mapResult.put("Id", "Cannot Send Id negative");
//			result.setResultMap(mapResult);
//			return result;
//		}
//		doctorrepo.updateDoctorSetUserNameWhereId(doctor.getUserName(), doctor.getId());
//		result.setStatus(0);
//		result.setResultMap(mapResult);
//		return result;
//
//	}

//	public Result updateDoctorSetPasswordWhereId(DoctorEntity doctor) {
//		Result result = new Result();
//		Map<String, Object> mapResult = new HashMap<>();
//		if (doctor.getUserName() == null || doctor.getUserName().isEmpty()) {
//			result.setStatus(1);
//			mapResult.put("NameEntity", "Cannot Send Name Entity Empty");
//			result.setResultMap(mapResult);
//			return result;
//		}
//		if (doctor.getId() != null) {
//			if (doctor.getId() < 0)
//				result.setStatus(1);
//			mapResult.put("Id", "Cannot Send Id negative");
//			result.setResultMap(mapResult);
//			return result;
//		}
//		doctorrepo.updateDoctorSetPasswordWhereId(doctor.getPassword(), doctor.getId());
//		result.setStatus(0);
//		result.setResultMap(mapResult);
//		return result;
//
//	}

//	public Result updateDoctorSetNationalIdWhereId(DoctorEntity doctor) {
//		Result result = new Result();
//		Map<String, Object> mapResult = new HashMap<>();
//
//		if (doctor.getId() != null) {
//			if (doctor.getId() == 0) 
//				result.setStatus(1);
//			mapResult.put("Id", "Cannot Send Id negative");
//			result.setResultMap(mapResult);
//			return result;
//		}
//		doctorrepo.updateDoctorSetNationalIdWhereId(doctor.getNationalID(), doctor.getId());
//		result.setStatus(0);
//		result.setResultMap(mapResult);
//		return result;
//
//	}

//	public Result updateDoctorSetSpecialtyWhereId(DoctorEntity doctor) {
//		Result result = new Result();
//		Map<String, Object> mapResult = new HashMap<>();
//		if (doctor.getUserName() == null || doctor.getUserName().isEmpty()) {
//			result.setStatus(1);
//			mapResult.put("doctor", "Cannot Send Name Entity Empty");
//			result.setResultMap(mapResult);
//			return result;
//		}
//		if (doctor.getId() != null) {
//			if (doctor.getId() < 0)
//				result.setStatus(1);
//			mapResult.put("Id", "Cannot Send Id negative");
//			result.setResultMap(mapResult);
//			return result;
//		}
//		doctorrepo.updateDoctorSetSpecialtyWhereId(doctor.getSpecialty(), doctor.getId());
//		result.setStatus(0);
//		result.setResultMap(mapResult);
//		return result;
//
//	}

//	public List<AppointmentEntity> findAllavalabli() {
//
//		return appointmentrepo.allAvalabliHours();
//	}

//	public List<AppointmentEntity> findBooked() {
//
//		return appointmentrepo.allBookedHours();
//	}

//
//	public Result updateDoctorSetPhoneNumberWhereId(DoctorEntity doctor) {
//		Result result = new Result();
//		Map<String, Object> mapResult = new HashMap<>();
//		if (doctor.getUserName() == null || doctor.getUserName().isEmpty()) {
//			result.setStatus(1);
//			mapResult.put("doctor", "Cannot Send Name Entity Empty");
//			result.setResultMap(mapResult);
//			return result;
//		}
//		if (doctor.getId() != null) {
//			if (doctor.getId() < 0)
//				result.setStatus(1);
//			mapResult.put("Id", "Cannot Send Id negative");
//			result.setResultMap(mapResult);
//			return result;
//		}
//		doctorrepo.updateDoctorSetPhoneNumberWhereId(doctor.getPhoneNumber(), doctor.getId());
//		result.setStatus(0);
//		result.setResultMap(mapResult);
//		return result;
//	}
