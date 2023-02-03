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
public class PatientService {
	@Autowired
	private DoctorRepo doctorrepo;
	@Autowired
	private PatientRepo patientrepo;
	@Autowired
	private Appointment appointmentrepo;
	@Autowired
	TokenUtility tokenUtility;


	private static final LocalTime START_TIME = LocalTime.of(8, 0);
	private static final LocalTime END_TIME = LocalTime.of(17, 0);
    //login
	public Result login(Login login) {
		Result result = new Result();
		Map<String, Object> mapResult = new HashMap<>();
		PatientEntity patient = patientrepo.findPatientByUserName(login.username);
		if (patient == null) {
			result.setStatus(1);
			result.setStatusDescription("patient Not Found ");
			return result;
		}
		if (!patient.getPassword().equalsIgnoreCase(login.password)) {

			result.setStatusDescription("Inncorect Password");
			result.setStatus(1);
			return result;

		}
		String token = tokenUtility.generateToken1(login.getUsername());
		mapResult.put("token", token);
		result.setStatus(0);
		result.setResultMap(mapResult);
		return result;
	}
	
	//add patient
	public Result addPatient(PatientEntity patient) {
		Result result = new Result();
		Map<String, Object> mapResult = new HashMap<>();
		if (patient.getUserName() == null || patient.getUserName().isEmpty()) {
			result.setStatus(1);
			result.setStatusDescription("Cannot Send Name Entity Empty.");
			mapResult.put("patient:", "Cannot Send Name Entity Empty.");
			result.setResultMap(mapResult);
			return result;
		}

		if (patient.getId() != null) {
			if (patient.getId() < 0)
				result.setStatus(1);
			result.setStatusDescription("Cannot Send Id negative.");
			mapResult.put("Id:", "Cannot Send Id negative.");
			result.setResultMap(mapResult);
			return result;
		}
		patientrepo.save(patient);
		result.setStatus(0);
		result.setResultMap(mapResult);
		return result;
	}

    //create appointment
	  public Result createAppointment(AppointmentEntity appointment1) {
			Result result = new Result();
			Map<String, Object> mapResult = new HashMap<>();
	        LocalDate ld= LocalDate.now();
			List<AppointmentEntity> appointmentEntity2 = appointmentrepo.findByDateAndTime(appointment1.getDate(), appointment1.getTime().minusMinutes(appointment1.getTime().getMinute()));

			if (appointment1.getTime().isBefore(START_TIME) || appointment1.getTime().isAfter(END_TIME)) {

				result.setStatus(1);
				
				result.setStatusDescription("The appointment is outside of the dentist's working hours.");
				return result;
			}

			if (appointment1.getDate().isBefore(ld)) {

				result.setStatus(1);
				
				result.setStatusDescription("You cannt create Appointment before The date of this day. ");
				return result;
			}
			Optional<DoctorEntity> chickdoctor= doctorrepo.findById(appointment1.getDoctor().getId());
			if(chickdoctor.isEmpty()) {
				result.setStatus(1);
				
				result.setStatusDescription("No doctor found . ");
				return result;
				}
			Optional<PatientEntity> chickpatient= patientrepo.findById(appointment1.getPatient().getId());
			if(chickpatient.isEmpty()) {
				result.setStatus(1);
				
				result.setStatusDescription("No patient found . ");
				return result;
				}
	       for(AppointmentEntity appointmentn:appointmentEntity2) {
			if (appointmentn.getDoctor().getId()==appointment1.getDoctor().getId() &&appointmentn.getDate().isEqual(appointment1.getDate())&&appointmentn.getTime().equals(appointment1.getTime().minusMinutes(appointment1.getTime().getMinute()))) {
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
	  //cancel appointment 
		public Result deleateAppointment(int id) {
			Result result = new Result();
			if (id <0) {
				
					result.setStatus(1);
				result.setStatusDescription("Cannot Send Id negative");
				return result;
			}
			if(appointmentrepo.findById(id)==null) {
				
				result.setStatus(1);
				result.setStatusDescription("No Appointment found");
				return result;	
				
			}
			appointmentrepo.deleteById(id);
			result.setStatus(0);
			result.setStatusDescription("Delete successful.");
			return result;
		}
		
		
	//	The Patient Can Show All Available Doctors Per the Current Date and Time.
	     public Result allTimeAvailable(LocalDate date ,LocalTime time1){
	    		Result result = new Result();
	    	Map<String, Object> mapResult = new HashMap<>();
     
            List<DoctorEntity>doctor=doctorrepo.findAll();
    
	       List<AppointmentEntity> listofappAppointmentEntities= appointmentrepo.findByDateAndTime(date,time1);
	       if (time1.isBefore(START_TIME)||time1.isAfter(END_TIME)) {
	  		   
	  			result.setStatus(1);	
				result.setStatusDescription("no  appointment");
				mapResult.put("time", "-The Dentist Working hours: 08:00 – 17:00. ");
				result.setResultMap(mapResult);
				return result;
	  		   
	  	   }
		
	 List<String> Doctorlist= doctor.stream().map(Doc->"Dr:"+Doc.getDname()+". id:"+Doc.getId()).collect(Collectors.toList());
	 List<String>booked=listofappAppointmentEntities.stream()
	      	.map(AppointmentEntity->"Dr:"+AppointmentEntity.getDoctor().getDname()+". id:"+AppointmentEntity.getDoctor().getId())
	  	       .collect(Collectors.toList());
	      
	         Doctorlist.removeAll(booked);    
	        if(Doctorlist.isEmpty()) {
	        	result.setStatus(1);	
				result.setStatusDescription("no Doctor Available in this time and date ");
				mapResult.put("Doctor", "");
				result.setResultMap(mapResult);
				return result;
	         }
	        result.setStatus(0);
	        result.setStatusDescription("Successful.");
	        mapResult.put("Doctors Available : ", Doctorlist );
	        result.setResultMap(mapResult);
	  	   return result;
	       }
	     
	     // The patient can see a list of all doctors
	     
	 	public List<String> listofAllPatient() {
	 		Result result = new Result();
	 		Map<String, Object> mapResult = new HashMap<>();
	 		List<DoctorEntity> listdoctor= new ArrayList<DoctorEntity>();
	 		
	 		listdoctor=doctorrepo.findAll();
	 		List<String>liststring=listdoctor.stream().map(DoctorEntity->DoctorEntity.getDname()).collect(Collectors.toList());
	 		
	 		if(liststring.isEmpty()) {

	 			result.setStatus(1);	
	 			result.setStatusDescription("no  Doctor");
	 			result.setResultMap(mapResult);
	 			return liststring;
	 		}
	 	
	 		
	 		result.setStatus(0);
	 		result.setResultMap(mapResult);
	 		return liststring ;
	 	}
    //update patient
	  public Result UpdatePatientProfile(PatientEntity patient1) {
		Result result = new Result();
		Map<String, Object> mapResult = new HashMap<>();
		PatientEntity patient = patientrepo.findPatientByUserName(patient1.getUserName());
		if (patient.getUserName() == null || patient.getUserName().isEmpty()) {
			result.setStatus(1);
			mapResult.put("patient", "Cannot Send Name Entity Empty");
			result.setResultMap(mapResult);
			return result;
		}
		if (patient.getId() != null) {
			if (patient.getId() < 0) {
				result.setStatus(1);
			mapResult.put("Id", "Cannot Send Id negative");
			result.setResultMap(mapResult);
			return result;
			}}
		patientrepo.save(patient1);
		result.setStatus(0);
		result.setResultMap(mapResult);
		return result;

	    }
	   // The patient can see a list of all doctors.
		public Result listofAlldoctor() {
			Result result = new Result();
			Map<String, Object> mapResult = new HashMap<>();
			List<DoctorEntity> listpatient= new ArrayList<DoctorEntity>();
			listpatient=doctorrepo.findAll();
			List<String>liststring=listpatient.stream().map(d->" Dr:"+d.getDname()+"  Specialty :"+d.getSpecialty()).collect(Collectors.toList());
			
			if(liststring.isEmpty()) {

				result.setStatus(1);	
				result.setStatusDescription("no  doctor");
				result.setResultMap(mapResult);
				return result;
			}
		
			
			result.setStatus(0);
			result.setStatusDescription("Successful.");
			mapResult.put("All Doctor Dentist", liststring);
			result.setResultMap(mapResult);
			return result ;
		}



	public String createCsvFile(LocalDate start, LocalDate end,int id) {
		List<AppointmentEntity> allAppointmentbtween = appointmentrepo.findByDateBetween(start, end);
		File file = new File(
				"C:\\Users\\talal\\Desktop\\repo\\patient.csv");
		try (PrintWriter writer = new PrintWriter(file);) {
			writer.println(" PatientName ,  DoctorName, Date , Time");
			allAppointmentbtween.forEach(x-> {if(x.getPatient().getId()==id) {
		writer.println( x.getPatient().getName()+ "," + x.getDoctor().getDname() + "," + x.getDate().toString()+","+x.getTime());}
			});
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}
}
//	public List<PatientEntity> findallPatient() {
//		return patientrepo.findAll();
//
//	}
//
//	public List<DoctorEntity> findalldoctor() {
//		Result result = new Result();
//		Map<String, Object> mapResult = new HashMap<>();
//			result.setStatus(0);
//			result.setResultMap(mapResult);
//			return doctorrepo.findAll();
//
//
//	}

//}
//
//	public Result updatePatientSetUserNameWhereId(PatientEntity patient) {
//		Result result = new Result();
//		Map<String, Object> mapResult = new HashMap<>();
//		if (patient.getUserName() == null || patient.getUserName().isEmpty()) {
//			result.setStatus(1);
//			mapResult.put("patient", "Cannot Send Name Entity Empty");
//			result.setResultMap(mapResult);
//			return result;
//		}
//		if (patient.getId() != null) {
//			if (patient.getId() < 0)
//				result.setStatus(1);
//			mapResult.put("Id", "Cannot Send Id negative");
//			result.setResultMap(mapResult);
//			return result;
//		}
//		patientrepo.updatePatientSetphoneNumberWhereId(patient.getPhoneNumber(), patient.getId());
//		result.setStatus(0);
//		result.setResultMap(mapResult);
//		return result;
//
//}