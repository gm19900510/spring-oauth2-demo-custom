package com.gm.authorization.server.custom.service;

import java.util.List;
import com.gm.authorization.server.custom.entity.CustomApproval;

public interface CustomApprovalService {

	List<CustomApproval> listClientDetails(String userId,CustomApproval customApproval);
	
	CustomApproval findApprovalById(Integer id);
}
