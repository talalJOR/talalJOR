package com.dentist.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dentist.entity.AppointmentEntity;
import com.dentist.entity.PatientEntity;

public interface Appointment extends JpaRepository<AppointmentEntity, Integer> {
	//AppointmentEntity findAppointmentById(int id);

	List<AppointmentEntity> findAppointmentByDoctorId(int id);

	List<AppointmentEntity> findByDateAndTime(LocalDate date, LocalTime time);

	List<AppointmentEntity> findByDoctorIdAndDate(int id, LocalDate date);

	PatientEntity findPatientById(int id);

	List<AppointmentEntity> findByDate(LocalDate ld);

	List<AppointmentEntity> findByDateBetween(LocalDate startdate, LocalDate enddate);
}
