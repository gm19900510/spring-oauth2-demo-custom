package com.gm.authorization.server.custom.service.impl;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.gm.authorization.server.custom.entity.CustomClientDetails;
import com.gm.authorization.server.custom.entity.SysRole;
import com.gm.authorization.server.custom.repository.CustomClientDetailsRepository;
import com.gm.authorization.server.custom.repository.SysRoleRepository;
import com.gm.authorization.server.custom.service.CustomClientDetailsService;

@Service
public class CustomClientDetailsServiceImpl implements CustomClientDetailsService {

	@Resource
	private CustomClientDetailsRepository clientDetailsRepository;

	@Resource
	private SysRoleRepository sysRoleRepository;

	@Override
	public void saveOrUpdate(CustomClientDetails clientDetails) {
		// TODO Auto-generated method stub
		clientDetailsRepository.save(clientDetails);
	}

	@Override
	public CustomClientDetails findOne(Integer id) {
		// TODO Auto-generated method stub
		return clientDetailsRepository.findById(id).get();
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		clientDetailsRepository.deleteById(id);
	}

	@Override
	public Page<CustomClientDetails> page(Specification<CustomClientDetails> specification, Pageable pageable) {
		// TODO Auto-generated method stub
		return clientDetailsRepository.findAll(specification, pageable);
	}

	@Override
	public List<SysRole> queryRoles() {
		// TODO Auto-generated method stub
		return sysRoleRepository.queryRoles();
	}

}
