package com.dentist.security;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.dentist.pojo.Result;
import com.dentist.utility.TokenUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ServiceInterceptor implements HandlerInterceptor {

	@Autowired
	private TokenUtility tokenUtility;
	@Autowired
	private ObjectMapper mapper;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String token = request.getHeader("token");
		Result result = new Result();
		Map<String, Object> resultMap = new HashMap<>();
		if (token == null || token.isEmpty()) {
			result.setStatus(401);
			resultMap.put("token", "token cannot be null or empty,please put the token in header");
			result.setResultMap(resultMap);
			// return result;
			String finalResult = mapper.writeValueAsString(result);
			response.setStatus(401);
			response.setContentType("application/json");

			try (PrintWriter writer = response.getWriter()) {
				writer.write(finalResult);
			}
			return false;
		} else {
			Result resultToken = tokenUtility.checkToken(token);
			if (resultToken.getStatus() == 0) {
				return true;
			} else {
				String finalResult = mapper.writeValueAsString(resultToken);
				response.setStatus(401);
				response.setContentType("application/json");

				try (PrintWriter writer = response.getWriter()) {
					writer.write(finalResult);
				}
				return false;
			}

		}

	}

}
