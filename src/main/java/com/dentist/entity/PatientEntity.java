package com.dentist.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "patient")
@Data
public class PatientEntity {
	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "Patient_username")
	private String userName;
	@Column(name = "Patient_password")
	private String password;
	@Column(name = "Patient_name")
	private String name;
	@Column(name = "age")
	private int age;
	@Column(name = "gender")
	private String gender;
	@Column(name = "Patient_phonenumber")
	private String phoneNumber;

	@OneToMany(mappedBy = "patient")
	@JsonIgnore
	List<AppointmentEntity> appolist;

}
