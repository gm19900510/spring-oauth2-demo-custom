package com.gm.authorization.server.custom.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.gm.authorization.server.custom.service.CustomClientDetailsService;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/management/client")
public class ClientController {

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

	@GetMapping(value = { "/", "", "/master" })
	public ModelAndView master(Principal principal) {
		List<SysRole> roles = customClientDetailsService.queryRoles();
		ModelAndView mav = new ModelAndView("client/master");
		mav.addObject("roles", roles);
		Map<String, String> grantTypes = new HashMap<String, String>();
		grantTypes.put("authorization_code", "授权码模式");
		grantTypes.put("password", "密码模式");
		grantTypes.put("implicit", "简化模式");
		grantTypes.put("client_credentials", "客户端模式");
		grantTypes.put("refresh_token", "刷新token");
		mav.addObject("grantTypes", grantTypes);
		return mav;
	}

	@SuppressWarnings("serial")
	@RequestMapping(value = "/data", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult<Page<CustomClientDetails>> data(
			@PageableDefault(page = 0, size = 10, sort = "createTime") Pageable pageable,
			CustomClientDetails customClientDetails, HttpServletRequest request) {
		ResponseResult<Page<CustomClientDetails>> responseResult = new ResponseResult<Page<CustomClientDetails>>();
		try {
			// 封装条件查询对象Specification
			Specification<CustomClientDetails> specification = new Specification<CustomClientDetails>() {

				@Override
				public Predicate toPredicate(Root<CustomClientDetails> root, CriteriaQuery<?> query,
						CriteriaBuilder cb) {
					List<Predicate> predicate = new ArrayList<>();

					if (StringUtils.isNotBlank(customClientDetails.getClientId())) {
						Predicate p = cb.like(root.get("clientId").as(String.class),
								"%" + customClientDetails.getClientId() + "%");
						predicate.add(p);
					}
					Predicate p = cb.notEqual(root.get("visiable").as(Integer.class), 1);
					predicate.add(p);
					Predicate[] pre = new Predicate[predicate.size()];
					return query.where(predicate.toArray(pre)).getRestriction();
				}
			};

			Page<CustomClientDetails> page = customClientDetailsService.page(specification, pageable);
			responseResult.setPath(request.getServletPath());
			responseResult.setData(page);
		} catch (Exception e) {
			responseResult.setStatus(GlobalConstant.ERROR);
			responseResult.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseResult.setErrorMessage(e.toString());
		}
		return responseResult;
	}

	/**
	 * @PreAuthorize使用说明 hasRole 角色授权：在自定义UserDetails返回Authority时需要加ROLE_前缀
	 *                   （数据库存储直接带有ROLE_前缀时，利用SimpleGrantedAuthority直接拼装；数据库存储时不带ROLE_前缀时，利用SimpleGrantedAuthority需加上ROLE_前缀拼装），
	 *                   Controller上使用时对于ROLE_前缀可加可不加；
	 * 
	 *                   hasAuthority
	 *                   权限授权：用户自定义的权限，返回的UserDetails的Authority只要与这里匹配就可以，这里不需要加ROLE_，名称保持一至即可。
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('clients:edit')")
	@ResponseBody
	public ResponseResult<Object> edit(CustomClientDetails clientDetails) {
		ResponseResult<Object> responseResult = new ResponseResult<Object>();
		try {
			Integer id = clientDetails.getId();
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (id != null && 0 != id) {
				CustomClientDetails object = customClientDetailsService.findOne(id);
				clientDetails.setCreateUser(object.getCreateUser());
				clientDetails.setCreateTime(object.getCreateTime());
				clientDetails.setUpdateUser(authentication.getName());
				clientDetails.setVisiable(object.getVisiable());
				clientDetails.setVersion(object.getVersion());// @Version数据库乐观锁控制，未设置Version时会把更新变为插入
			} else {
				clientDetails.setCreateUser(authentication.getName());
			}
			// 将原ClientSecret保存至备注字段
			clientDetails.setRemarks(clientDetails.getClientSecret());
			String pwdEncode = passwordEncoder.encode(clientDetails.getClientSecret());
			clientDetails.setClientSecret(pwdEncode);
			customClientDetailsService.saveOrUpdate(clientDetails);
		} catch (Exception e) {
			responseResult.setStatus(GlobalConstant.ERROR);
			responseResult.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseResult.setErrorMessage(e.toString());
		}
		return responseResult;
	}

	@RequestMapping(value = "/details/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseResult<CustomClientDetails> get(@PathVariable(value = "id") Integer id) {
		ResponseResult<CustomClientDetails> responseResult = new ResponseResult<CustomClientDetails>();
		try {
			CustomClientDetails object = customClientDetailsService.findOne(id);
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

			CustomClientDetails clientDetails = customClientDetailsService.findOne(id);
			clientDetails.setCreateUser(clientDetails.getCreateUser());
			clientDetails.setCreateTime(clientDetails.getCreateTime());
			clientDetails.setUpdateUser(authentication.getName());
			clientDetails.setVisiable(visiable);
			customClientDetailsService.saveOrUpdate(clientDetails);

		} catch (Exception e) {
			responseResult.setStatus(GlobalConstant.ERROR);
			responseResult.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseResult.setErrorMessage(e.toString());
		}
		return responseResult;
	}

}
