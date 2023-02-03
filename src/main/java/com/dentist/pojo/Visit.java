package com.dentist.pojo;

import javax.validation.constraints.NotBlank;

import lombok.Data;
@Data
public class Visit {
	@NotBlank(message = "appointmentId cannot be null or empty")
	public int appointmentId ;
	@NotBlank(message = "status cannot be null or empty")
	public boolean status ;
}
