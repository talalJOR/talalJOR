package com.dentist.entity;


import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Table(name = "appointment")
@Data
public class AppointmentEntity {
	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "doctorapp" , referencedColumnName = "id")
	private DoctorEntity doctor;
	@ManyToOne
	@JoinColumn(name = "patientapp" , referencedColumnName = "id")
    private PatientEntity patient;
	
	@JsonFormat( pattern = "dd/MM/yyyy")
	@Column(name = "date1")
	private LocalDate date;
	
	@JsonFormat( pattern = "HH:mm")
	@Column(name = "time1")
	private LocalTime time;

	@Column(name = "is_Visited")
	private boolean isVisited;
	@Column(name = "is_Booked")
	private boolean isBooked;

	public AppointmentEntity CreateAppointment(DoctorEntity doctor, PatientEntity patient, 
			LocalDate date,LocalTime time) {

		this.setDoctor(doctor);
		this.setPatient(patient);
	    this.setTime(time);
		this.setDate(date);
		return this;
	}

//	
}
