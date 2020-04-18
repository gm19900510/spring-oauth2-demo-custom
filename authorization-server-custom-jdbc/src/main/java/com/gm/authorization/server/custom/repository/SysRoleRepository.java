package com.gm.authorization.server.custom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import com.gm.authorization.server.custom.entity.SysRole;

/**
 * 
 */
public interface SysRoleRepository extends JpaSpecificationExecutor<SysRole>, JpaRepository<SysRole, Integer> {

	@Query(nativeQuery = true, value = "select * from sys_role where visiable=0")
	List<SysRole> queryRoles();
	
	/*@Query(nativeQuery = true, value = "select * from sys_role where visiable=0 and role_code=?1")
	SysRole findSysRoleByCode(String code);*/
	
	SysRole findByRoleCode(String roleCode);
}
