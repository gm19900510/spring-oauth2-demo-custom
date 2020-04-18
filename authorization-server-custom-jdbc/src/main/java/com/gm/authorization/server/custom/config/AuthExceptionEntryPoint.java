package com.gm.authorization.server.custom.config;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm.authorization.server.custom.domain.GlobalConstant;
import com.gm.authorization.server.custom.domain.ResponseResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 无效token异常处理
 * 
 * @author Administrator
 *
 */
@Component("authExceptionEntryPoint")
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws ServletException {
		response.setHeader("Content-Type", "application/json;charset=UTF-8");

		ResponseResult<Object> responseMessage = new ResponseResult<>();
		responseMessage.setStatus(GlobalConstant.ERROR);
		responseMessage.setErrorCode(HttpStatus.UNAUTHORIZED.value());
		responseMessage.setErrorMessage(authException.getMessage());
		responseMessage.setPath(request.getServletPath());

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonGenerator jsonGenerator = mapper.getFactory().createGenerator(response.getOutputStream(),
					JsonEncoding.UTF8);
			mapper.writeValue(jsonGenerator, responseMessage);
		} catch (Exception e) {
			throw new ServletException();
		}
	}
}