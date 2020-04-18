package com.gm.authorization.server.custom.entity;

import java.util.Date;

import lombok.Data;

@Data
public class CustomApproval {

	private Integer id;
	
	private String status;

	private String userId;

	private String clientId;

	private String scope;

	private Date expiresAt;

	private Date lastUpdatedAt;

}
