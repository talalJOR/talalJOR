package com.dentist.pojo;

import java.util.Map;



import lombok.Data;

@Data
public class Result {
	private int status;
	private String statusDescription;
	private Map<String, Object> resultMap;
	
}
