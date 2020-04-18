package com.gm.authorization.server.custom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gm.authorization.server.custom.entity.LoginHistory;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Integer> {
    
}
