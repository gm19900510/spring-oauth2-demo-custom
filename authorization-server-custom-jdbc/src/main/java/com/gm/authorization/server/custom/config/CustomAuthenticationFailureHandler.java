package com.gm.authorization.server.custom.config;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm.authorization.server.custom.domain.GlobalConstant;
import com.gm.authorization.server.custom.domain.ResponseResult;
import com.gm.authorization.server.custom.entity.LoginHistory;
import com.gm.authorization.server.custom.service.LoginHistoryService;
import com.gm.authorization.server.custom.utils.ClientIPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	LoginHistoryService loginHistoryService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException {
		String username = request.getParameter("username");
		log.debug(exception.toString());

		LoginHistory loginHistory = new LoginHistory();
		loginHistory.setUsername(username);
		loginHistory.setIp(ClientIPUtils.getIpAddress(request));
		loginHistory.setDevice(request.getHeader("User-Agent"));
		loginHistory.setRemarks(exception.getMessage());
		loginHistory.setStatus(GlobalConstant.ERROR);
		loginHistory.setCreateUser(GlobalConstant.SYSTEM_USER);
		
		loginHistoryService.asyncCreate(loginHistory);

		boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
		if (isAjax) {

			response.setHeader("Content-Type", "application/json;charset=UTF-8");
			try {

				ResponseResult<Object> responseMessage = new ResponseResult<>();
				responseMessage.setStatus(GlobalConstant.ERROR);
				responseMessage.setErrorCode(HttpStatus.BAD_REQUEST.value());
				responseMessage.setErrorMessage(exception.getMessage());
				ObjectMapper mapper = new ObjectMapper();
				JsonGenerator jsonGenerator = mapper.getFactory().createGenerator(response.getOutputStream(),
						JsonEncoding.UTF8);
				mapper.writeValue(jsonGenerator, responseMessage);

			} catch (Exception ex) {
				if (log.isErrorEnabled()) {
					log.error("Could not write JSON:", ex);
				}
				throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
			}
		} else {
			String encodedMessage = "";
			try {
				encodedMessage = URLEncoder.encode(exception.getMessage(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				if (log.isErrorEnabled()) {
					log.error("encodedMessage", e);
				}
			}

			response.sendRedirect(
					GlobalConstant.LOGIN_FAILURE_URL + "?authentication_error=true&error=" + encodedMessage);

		}
	}
}
