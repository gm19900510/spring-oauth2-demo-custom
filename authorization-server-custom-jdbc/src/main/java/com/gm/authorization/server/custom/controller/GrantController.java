package com.gm.authorization.server.custom.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 * 自定义授权配置
 * 
 * @author Administrator
 *
 */
@Controller
@SessionAttributes("authorizationRequest") // 必须配置
public class GrantController {

	/**
	 * 在自定义授权页面中必须加上
	 * <input type="hidden" name="_csrf" th:value="${_csrf.getToken()}"/>
	 * 不然点击授权后会出现OAuth Server Error异常
	 * 
	 */

	@RequestMapping("/custom/confirm_access")
	public ModelAndView getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) throws Exception {
		AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
		ModelAndView view = new ModelAndView();
		view.setViewName("grant");
		@SuppressWarnings("unchecked")
		Map<String, String> scopes = (Map<String, String>) (model.containsKey("scopes") ? model.get("scopes")
				: request.getAttribute("scopes"));
		List<String> scopeList = new ArrayList<String>();
		for (String scope : scopes.keySet()) {
			scopeList.add(scope);
		}
		model.put("scopeList", scopeList);
		view.addObject("clientId", authorizationRequest.getClientId());
		return view;
	}
}
