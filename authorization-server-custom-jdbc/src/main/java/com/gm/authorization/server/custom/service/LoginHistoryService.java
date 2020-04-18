package com.gm.authorization.server.custom.service;

import com.gm.authorization.server.custom.entity.LoginHistory;

public interface LoginHistoryService {

	void asyncCreate(LoginHistory loginHistory);

}
