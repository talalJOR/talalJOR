package com.dentist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.dentist.entity.DoctorEntity;

@Repository
public interface DoctorRepo extends JpaRepository<DoctorEntity, Integer> {

	DoctorEntity findDoctorByUserName(String name);

    DoctorEntity findDoctorById(int id);

}
