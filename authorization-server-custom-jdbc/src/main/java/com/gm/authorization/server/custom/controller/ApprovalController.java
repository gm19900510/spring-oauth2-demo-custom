package com.gm.authorization.server.custom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.gm.authorization.server.custom.domain.AuthorityPropertyEditor;
import com.gm.authorization.server.custom.domain.GlobalConstant;
import com.gm.authorization.server.custom.domain.ResponseResult;
import com.gm.authorization.server.custom.domain.SplitCollectionEditor;
import com.gm.authorization.server.custom.entity.CustomApproval;
import com.gm.authorization.server.custom.service.CustomApprovalService;
import com.gm.authorization.server.custom.service.CustomClientDetailsService;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/management/approval")
public class ApprovalController {

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Collection.class, new SplitCollectionEditor(Set.class, ","));
		binder.registerCustomEditor(GrantedAuthority.class, new AuthorityPropertyEditor());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
	}

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	CustomClientDetailsService customClientDetailsService;

	@Autowired
	private ApprovalStore approvalStore;
	
	@Autowired
	private CustomApprovalService customApprovalService;

	@Autowired
	private TokenStore tokenStore;

	@GetMapping(value = { "/", "", "/master" })
	public ModelAndView master(Principal principal) {
		ModelAndView mav = new ModelAndView("approval/master");
		return mav;
	}

	@RequestMapping(value = "/data", method = RequestMethod.GET)
	@ResponseBody
	public ResponseResult<List<CustomApproval>> data(HttpServletRequest request, Principal principal,CustomApproval customApproval) {
		ResponseResult<List<CustomApproval>> responseResult = new ResponseResult<List<CustomApproval>>();
		try {

			List<CustomApproval> approvals = customApprovalService.listClientDetails(principal.getName(),customApproval);

			responseResult.setPath(request.getServletPath());
			responseResult.setData(approvals);
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(GlobalConstant.ERROR);
			responseResult.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseResult.setErrorMessage(e.toString());
		}
		return responseResult;
	}

	@RequestMapping(value = "/revoke/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseResult<Object> revokApproval(@PathVariable(value = "id") Integer id) {
		ResponseResult<Object> responseResult = new ResponseResult<Object>();
		try {
			CustomApproval customApproval = customApprovalService.findApprovalById(id);
			ApprovalStatus status = null;
			if (customApproval.getStatus().endsWith(ApprovalStatus.APPROVED.name())) {
				status = ApprovalStatus.APPROVED;
			} else {
				status = ApprovalStatus.DENIED;
			}
			approvalStore.revokeApprovals(Arrays.asList(new Approval(customApproval.getUserId(),
					customApproval.getClientId(), customApproval.getScope(), customApproval.getExpiresAt(), status)));
			tokenStore.findTokensByClientIdAndUserName(customApproval.getClientId(), customApproval.getUserId())
					.forEach(tokenStore::removeAccessToken);
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(GlobalConstant.ERROR);
			responseResult.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseResult.setErrorMessage(e.toString());
		}
		return responseResult;
	}

}
