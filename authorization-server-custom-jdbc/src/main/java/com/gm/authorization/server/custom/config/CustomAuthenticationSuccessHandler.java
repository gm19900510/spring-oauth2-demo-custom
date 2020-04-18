package com.gm.authorization.server.custom.config;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm.authorization.server.custom.domain.GlobalConstant;
import com.gm.authorization.server.custom.domain.ResponseResult;
import com.gm.authorization.server.custom.entity.LoginHistory;
import com.gm.authorization.server.custom.service.LoginHistoryService;
import com.gm.authorization.server.custom.utils.ClientIPUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	@Autowired
	LoginHistoryService loginHistoryService;

	RequestCache requestCache = new HttpSessionRequestCache();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String redirectUrl = "";
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		if (savedRequest != null && StringUtils.isNotEmpty(savedRequest.getRedirectUrl())) {
			redirectUrl = savedRequest.getRedirectUrl();
		}

		// 移除验证码
		request.getSession().removeAttribute(GlobalConstant.VERIFICATION_CODE);

		String username = request.getParameter("username");

		LoginHistory loginHistory = new LoginHistory();
		loginHistory.setUsername(username);
		loginHistory.setIp(ClientIPUtils.getIpAddress(request));
		loginHistory.setDevice(request.getHeader("User-Agent"));
		loginHistory.setStatus(GlobalConstant.SUCCESS);
		loginHistory.setCreateUser(GlobalConstant.SYSTEM_USER);
		if (savedRequest != null && StringUtils.isNotEmpty(savedRequest.getRedirectUrl())) {
			String[] client_id = savedRequest.getParameterMap().get("client_id");
			if(ArrayUtils.isNotEmpty(client_id)) {
				loginHistory.setClientId(ArrayUtils.toString(client_id));
			}
		}
		
		loginHistoryService.asyncCreate(loginHistory);

		boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
				|| "apiLogin".equals(request.getHeader("api-login"));

		if (isAjax) {
			response.setHeader("Content-Type", "application/json;charset=UTF-8");
			try {

				ResponseResult<Object> responseMessage = new ResponseResult<>();
				responseMessage.setStatus(GlobalConstant.SUCCESS);
				ObjectMapper mapper = new ObjectMapper();
				JsonGenerator jsonGenerator = mapper.getFactory().createGenerator(response.getOutputStream(),
						JsonEncoding.UTF8);
				mapper.writeValue(jsonGenerator, responseMessage);

			} catch (Exception ex) {
				if (logger.isErrorEnabled()) {
					logger.error("Could not write JSON:", ex);
				}
				throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
			}
		} else {
			if (StringUtils.isNotEmpty(redirectUrl)) {
				super.onAuthenticationSuccess(request, response, authentication);
			} else {
				response.sendRedirect("/");
			}
		}
	}
}
