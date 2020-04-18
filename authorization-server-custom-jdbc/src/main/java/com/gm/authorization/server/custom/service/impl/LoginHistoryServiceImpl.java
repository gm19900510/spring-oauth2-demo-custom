package com.gm.authorization.server.custom.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gm.authorization.server.custom.entity.LoginHistory;
import com.gm.authorization.server.custom.repository.LoginHistoryRepository;
import com.gm.authorization.server.custom.service.LoginHistoryService;

@Service
public class LoginHistoryServiceImpl implements LoginHistoryService {

	@Autowired
	LoginHistoryRepository loginHistoryRepository;
	
	@Transactional
    @Async
	@Override
	public void asyncCreate(LoginHistory loginHistory) {	
		loginHistoryRepository.save(loginHistory);
	}

}
