package com.dentist.pojo;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data

public class Login {

	@NotBlank(message = "Uername cannot be null or empty")
	public String username;

	@NotBlank(message = "Uername cannot be null or empty")
	public String password;


}
