package com.gm.authorization.server.custom.config;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm.authorization.server.custom.domain.GlobalConstant;
import com.gm.authorization.server.custom.domain.ResponseResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限不足异常处理
 * 
 * @author Administrator
 *
 */
@Component("customAccessDeniedHandler")
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		response.setHeader("Content-Type", "application/json;charset=UTF-8");

		ResponseResult<Object> responseMessage = new ResponseResult<>();
		responseMessage.setStatus(GlobalConstant.ERROR);
		responseMessage.setErrorCode(HttpStatus.BAD_REQUEST.value());
		responseMessage.setErrorMessage(accessDeniedException.getMessage());
		responseMessage.setPath(request.getServletPath());

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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