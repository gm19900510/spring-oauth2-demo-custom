package com.gm.authorization.server.custom.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.gm.authorization.server.custom.entity.CustomClientDetails;
import com.gm.authorization.server.custom.entity.SysRole;

public interface CustomClientDetailsService {

	void saveOrUpdate(CustomClientDetails clientDetails);

	CustomClientDetails findOne(Integer id);

	void delete(Integer id);

	Page<CustomClientDetails> page(Specification<CustomClientDetails> specification, Pageable pageable);

	List<SysRole> queryRoles();
	
}
