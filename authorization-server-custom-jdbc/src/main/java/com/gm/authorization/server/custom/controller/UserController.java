package com.gm.authorization.server.custom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.gm.authorization.server.custom.domain.AuthorityPropertyEditor;
import com.gm.authorization.server.custom.domain.GlobalConstant;
import com.gm.authorization.server.custom.domain.ResponseResult;
import com.gm.authorization.server.custom.domain.SplitCollectionEditor;
import com.gm.authorization.server.custom.entity.CustomClientDetails;
import com.gm.authorization.server.custom.entity.SysRole;
import com.gm.authorization.server.custom.entity.SysUser;
import com.gm.authorization.server.custom.service.CustomClientDetailsService;
import com.gm.authorization.server.custom.service.UserService;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/management/user")
public class UserController {

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
	UserService userService;

	@GetMapping(value = { "/", "", "/master" })
	public ModelAndView master(Principal principal) {
		List<SysRole> roles = customClientDetailsService.queryRoles();
		ModelAndView mav = new ModelAndView("user/master");
		mav.addObject("roles", roles);
		return mav;
	}

	@RequestMapping(value = "/data", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult<Page<Map<String, Object>>> data(
			@PageableDefault(page = 0, size = 10, sort = "create_time") Pageable pageable,
			CustomClientDetails customClientDetails, HttpServletRequest request) {
		ResponseResult<Page<Map<String, Object>>> responseResult = new ResponseResult<Page<Map<String, Object>>>();
		try {
			String username = request.getParameter("username");
			Page<Map<String, Object>> page = userService.page(username, pageable);
			responseResult.setPath(request.getServletPath());
			responseResult.setData(page);
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(GlobalConstant.ERROR);
			responseResult.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseResult.setErrorMessage(e.toString());
		}
		return responseResult;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult<Object> edit(SysUser sysUser, HttpServletRequest request,String roles) {
		ResponseResult<Object> responseResult = new ResponseResult<Object>();
		try {
			Integer id = sysUser.getId();
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (id != null && 0 != id) {
				SysUser object = userService.findOne(id);
				sysUser.setCreateUser(object.getCreateUser());
				sysUser.setCreateTime(object.getCreateTime());
				sysUser.setUpdateUser(authentication.getName());
				sysUser.setVisiable(object.getVisiable());
				sysUser.setVersion(object.getVersion());// @Version数据库乐观锁控制，未设置Version时会把更新变为插入
			} else {
				sysUser.setCreateUser(authentication.getName());
			}
			// 将加密密码保存至备注字段
			String pwdEncode = passwordEncoder.encode(sysUser.getPassword());
			sysUser.setRemarks(pwdEncode);

			userService.saveOrUpdate(sysUser, roles);
		} catch (Exception e) {
			e.printStackTrace();
			responseResult.setStatus(GlobalConstant.ERROR);
			responseResult.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseResult.setErrorMessage(e.toString());
		}
		return responseResult;
	}

	@RequestMapping(value = "/details/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseResult<Map<String, Object>> get(@PathVariable(value = "id") Integer id) {
		ResponseResult<Map<String, Object>> responseResult = new ResponseResult<Map<String, Object>>();
		try {
			Map<String, Object> object = userService.findUserById(id);
			responseResult.setData(object);
		} catch (Exception e) {
			responseResult.setStatus(GlobalConstant.ERROR);
			responseResult.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseResult.setErrorMessage(e.toString());
		}
		return responseResult;
	}

	@RequestMapping(value = "/sign/{id}/{visiable}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseResult<CustomClientDetails> sign(@PathVariable(value = "id") Integer id,
			@PathVariable(value = "visiable") Integer visiable) {
		ResponseResult<CustomClientDetails> responseResult = new ResponseResult<CustomClientDetails>();
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			SysUser sysUer = userService.findOne(id);
			sysUer.setCreateUser(sysUer.getCreateUser());
			sysUer.setCreateTime(sysUer.getCreateTime());
			sysUer.setUpdateUser(authentication.getName());
			sysUer.setVisiable(visiable);
			userService.saveOrUpdate(sysUer);

		} catch (Exception e) {
			responseResult.setStatus(GlobalConstant.ERROR);
			responseResult.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseResult.setErrorMessage(e.toString());
		}
		return responseResult;
	}

}
