package com.gm.authorization.server.custom.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm.authorization.server.custom.domain.GlobalConstant;
import com.gm.authorization.server.custom.domain.ResponseResult;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

/**
 * 认证客户端信息参数处理 filter
 *
 */
@Component
public class CustomBasicAuthenticationFilter extends OncePerRequestFilter {

	private static final String OAUTH_TOKEN_URI = "/oauth/token";

	@Autowired
	private ClientDetailsService clientDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (!request.getRequestURI().equals(OAUTH_TOKEN_URI)) {
			filterChain.doFilter(request, response);
			return;
		}

		String[] clientDetails = this.isHasClientDetails(request);

		if (clientDetails == null) {
			responseWriter(HttpStatus.UNAUTHORIZED.value(), "No client credentials presented", request, response);
			return;
		}
		this.handle(request, response, clientDetails, filterChain);
	}

	private void handle(HttpServletRequest request, HttpServletResponse response, String[] clientDetails,
			FilterChain filterChain) throws IOException, ServletException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated()) {
			filterChain.doFilter(request, response);
			return;
		}
		ClientDetails details = null;
		try {
			details = clientDetailsService.loadClientByClientId(clientDetails[0]);
		} catch (Exception e) {
			if (details == null) {
				responseWriter(HttpStatus.UNAUTHORIZED.value(), "No client with requested id:" + clientDetails[0],
						request, response);
				return;
			}
		}

		if (details != null && !passwordEncoder.matches(clientDetails[1], details.getClientSecret())) {
			responseWriter(HttpStatus.UNAUTHORIZED.value(), "Bad client credentials secret:" + clientDetails[1],
					request, response);
			return;
		}

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(details.getClientId(),
				details.getClientSecret(), details.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);
		filterChain.doFilter(request, response);
	}

	/**
	 * 判断请求头中是否包含client信息，不包含返回null
	 */
	private String[] isHasClientDetails(HttpServletRequest request) {
		String[] params = null;
		try {
			String header = request.getHeader(HttpHeaders.AUTHORIZATION);
			if (header != null) {
				String basic = header.substring(0, 5);
				if (basic.toLowerCase().contains("basic")) {
					String tmp = header.substring(6);
					String defaultClientDetails = new String(Base64.getDecoder().decode(tmp));
					String[] clientArrays = defaultClientDetails.split(":");
					if (clientArrays.length != 2) {
						return params;
					} else {
						params = clientArrays;
					}
				}
			}
			String client_id = request.getParameter("client_id");
			String client_secret = request.getParameter("client_secret");
			if (header == null && client_id != null) {
				params = new String[] { client_id, client_secret };
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

	public static void responseWriter(int code, String message, HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		response.setHeader("Content-Type", "application/json;charset=UTF-8");

		ResponseResult<Object> responseMessage = new ResponseResult<>();
		responseMessage.setStatus(GlobalConstant.ERROR);
		responseMessage.setErrorCode(code);
		responseMessage.setErrorMessage(message);
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
