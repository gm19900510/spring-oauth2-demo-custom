package com.gm.authorization.server.custom.service;

import java.util.List;
import com.gm.authorization.server.custom.entity.SysPermission;

/**
 * 
 */
public interface PermissionService {

    List<SysPermission> findByUserId(Integer userId);

}
