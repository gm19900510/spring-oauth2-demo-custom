package com.gm.authorization.server.custom.repository;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import com.gm.authorization.server.custom.entity.SysUser;


/**
 * 
 */
public interface SysUserRepository extends JpaSpecificationExecutor<SysUser>, JpaRepository<SysUser, Integer> {

    SysUser findByUsername(String username);
    
    @Query(nativeQuery = true, value = "select su.*,tab.role_names,role_ids from sys_user su left join (select sur.user_id,GROUP_CONCAT(role_name SEPARATOR ',') role_names,GROUP_CONCAT(sr.id SEPARATOR ',') role_ids from sys_user_role sur left join sys_role sr on sur.role_id = sr.id and sr.visiable=0 group by user_id) tab on su.id = tab.user_id where visiable!='1' and if ((?1 is null) or (?1 = ''),1=1,username = ?1)")
    Page<Map<String,Object>> queryPage(String username, Pageable pageable);
     
    @Query(nativeQuery = true, value = "select su.*,tab.role_names,role_ids from sys_user su left join (select sur.user_id,GROUP_CONCAT(role_name SEPARATOR ',') role_names,GROUP_CONCAT(sr.id SEPARATOR ',') role_ids from sys_user_role sur left join sys_role sr on sur.role_id = sr.id and sr.visiable=0 group by user_id) tab on su.id = tab.user_id where su.id = ?1")
    Map<String,Object> findUserById(Integer id);
}
