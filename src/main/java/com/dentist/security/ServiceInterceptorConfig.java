package com.dentist.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class ServiceInterceptorConfig implements WebMvcConfigurer {
	@Autowired
	private ServiceInterceptor serviceInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(serviceInterceptor).addPathPatterns("/**").excludePathPatterns("/Doctor/login")
				.excludePathPatterns("/patient/login").excludePathPatterns("/Doctor/Register")
				.excludePathPatterns("/patient/Register").excludePathPatterns("/Doctor/allbooktime");
	}

}
