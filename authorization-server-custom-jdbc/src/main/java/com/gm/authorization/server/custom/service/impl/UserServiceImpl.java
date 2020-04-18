package com.gm.authorization.server.custom.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gm.authorization.server.custom.entity.SysRole;
import com.gm.authorization.server.custom.entity.SysUser;
import com.gm.authorization.server.custom.entity.SysUserRole;
import com.gm.authorization.server.custom.repository.SysRoleRepository;
import com.gm.authorization.server.custom.repository.SysUserRepository;
import com.gm.authorization.server.custom.repository.SysUserRoleRepository;
import com.gm.authorization.server.custom.service.UserService;

/**
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private SysUserRepository sysUserRepository;

	@Autowired
	private SysRoleRepository sysRoleRepository;

	@Autowired
	private SysUserRoleRepository sysUserRoleRepository;

	@Override
	public SysUser getByUsername(String username) {
		return sysUserRepository.findByUsername(username);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveOrUpdate(SysUser user, String roles) {
		// TODO Auto-generated method stub
		Integer userId = user.getId();
		sysUserRepository.save(user);
		if (userId != null && userId != 0) {
			Map<String, Object> map = sysUserRepository.findUserById(userId);
			if (map != null && map.get("role_ids") != null) {
				String role_ids = map.get("role_ids").toString();
				List<String> old_roleIds = new ArrayList<String>(Arrays.asList(role_ids.split(",")));
				// List<String> old_roleIds_copy = new ArrayList<String>(old_roleIds.size());
				// List<String> old_roleIds_copy = Arrays.asList(new String[old_roleIds.size()]);
				List<String> old_roleIds_copy = new ArrayList<String>(old_roleIds);
				Collections.copy(old_roleIds_copy, old_roleIds);

				if (StringUtils.isNoneEmpty(roles)) {
					if (ArrayUtils.isNotEmpty(roles.split(","))) {
						List<String> roleIds = new ArrayList<String>(Arrays.asList(roles.split(",")));
						// List<String> roleIds_copy = new ArrayList<String>(roleIds.size());
						// List<String> roleIds_copy = Arrays.asList(new String[roleIds.size()]);
						List<String> roleIds_copy = new ArrayList<String>(roleIds);
						Collections.copy(roleIds_copy, roleIds);

						// 求出新增的角色
						roleIds_copy.removeAll(old_roleIds_copy);
						List<SysUserRole> entities = new ArrayList<SysUserRole>();
						for (int i = 0; i < roleIds_copy.size(); i++) {
							SysRole sysRole = sysRoleRepository.getOne(Integer.parseInt(roleIds_copy.get(i)));
							SysUserRole sysUserRole = new SysUserRole();
							sysUserRole.setRoleId(sysRole.getId());
							sysUserRole.setUserId(user.getId());
							entities.add(sysUserRole);
						}
						if (entities != null && entities.size() > 0) {
							sysUserRoleRepository.saveAll(entities);
						}

						// 求出取消的角色
						old_roleIds.removeAll(roleIds);
						for (int i = 0; i < old_roleIds.size(); i++) {
							sysUserRoleRepository.deleteByUserIdAndRoleId(user.getId(), Integer.parseInt(old_roleIds.get(i)));
						}
					}
				}
			} else {
				if (StringUtils.isNoneEmpty(roles)) {
					if (ArrayUtils.isNotEmpty(roles.split(","))) {
						List<String> roleIds = Arrays.asList(roles.split(","));
						List<SysUserRole> entities = new ArrayList<SysUserRole>();
						for (int i = 0; i < roleIds.size(); i++) {
							SysRole sysRole = sysRoleRepository.getOne(Integer.parseInt(roleIds.get(i)));
							SysUserRole sysUserRole = new SysUserRole();
							sysUserRole.setRoleId(sysRole.getId());
							sysUserRole.setUserId(user.getId());
							entities.add(sysUserRole);
						}
						if (entities != null && entities.size() > 0) {
							sysUserRoleRepository.saveAll(entities);
						}
					}
				}
			}

		} else {
			if (StringUtils.isNoneEmpty(roles)) {
				if (ArrayUtils.isNotEmpty(roles.split(","))) {
					List<String> roleIds = Arrays.asList(roles.split(","));
					List<SysUserRole> entities = new ArrayList<SysUserRole>();
					for (int i = 0; i < roleIds.size(); i++) {
						SysRole sysRole = sysRoleRepository.getOne(Integer.parseInt(roleIds.get(i)));
						SysUserRole sysUserRole = new SysUserRole();
						sysUserRole.setRoleId(sysRole.getId());
						sysUserRole.setUserId(user.getId());
						entities.add(sysUserRole);
					}
					if (entities != null && entities.size() > 0) {
						sysUserRoleRepository.saveAll(entities);
					}
				}
			}
		}
	}

	@Override
	public void saveOrUpdate(SysUser user) {
		// TODO Auto-generated method stub
		sysUserRepository.save(user);
	}

	@Override
	public SysUser findOne(Integer id) {
		// TODO Auto-generated method stub
		return sysUserRepository.findById(id).get();
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		sysUserRepository.deleteById(id);
	}

	@Override
	public Page<Map<String, Object>> page(String username, Pageable pageable) {
		// TODO Auto-generated method stub
		return sysUserRepository.queryPage(username, pageable);
	}

	@Override
	public Map<String, Object> findUserById(Integer id) {
		// TODO Auto-generated method stub
		return sysUserRepository.findUserById(id);
	}

}
