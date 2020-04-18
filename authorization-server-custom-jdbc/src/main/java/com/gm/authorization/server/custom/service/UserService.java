package com.gm.authorization.server.custom.service;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.gm.authorization.server.custom.entity.SysUser;

/**
 * 
 */
public interface UserService {

	SysUser getByUsername(String username);

	void saveOrUpdate(SysUser user, String roles);
	
	void saveOrUpdate(SysUser user);

	SysUser findOne(Integer id);

	void delete(Integer id);

	Page<Map<String,Object>> page(String username, Pageable pageable);
	
	Map<String,Object> findUserById(Integer id);
}
