package com.dentist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.dentist.entity.PatientEntity;

@Repository
public interface PatientRepo extends JpaRepository<PatientEntity, Integer> {
	PatientEntity findPatientById(int id);

	PatientEntity findPatientByUserName(String name);
}

