package com.gm.authorization.server.custom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.gm.authorization.server.custom.entity.SysUserRole;
import java.util.List;

/**
 *
 */
public interface SysUserRoleRepository extends JpaSpecificationExecutor<SysUserRole>, JpaRepository<SysUserRole, Integer> {

    List<SysUserRole> findByUserId(Integer userId);
    
    @Modifying
	@Query(value = "delete from SysUserRole where userId =?1 and roleId =?2")
    void deleteByUserIdAndRoleId(Integer userId,Integer roleId);
}
