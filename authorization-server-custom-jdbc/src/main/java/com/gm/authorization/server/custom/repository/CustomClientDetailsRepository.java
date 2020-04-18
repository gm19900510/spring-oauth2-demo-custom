package com.gm.authorization.server.custom.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import com.gm.authorization.server.custom.entity.CustomClientDetails;

public interface CustomClientDetailsRepository extends CrudRepository<CustomClientDetails, Integer>,JpaSpecificationExecutor<CustomClientDetails> {
	CustomClientDetails findByClientId(String clientId);

}
