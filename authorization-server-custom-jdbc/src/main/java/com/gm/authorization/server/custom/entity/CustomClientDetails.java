package com.gm.authorization.server.custom.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "oauth_client_details")
@EqualsAndHashCode(callSuper=false)
@EntityListeners(AuditingEntityListener.class)
public class CustomClientDetails extends BaseEntity {

	private String clientId;
	private String resourceIds;
	private String clientSecret;
	private String scope;
	private String authorizedGrantTypes;
	private String webServerRedirectUri;
	private String authorities;
	private Integer accessTokenValidity = 24 * 60 * 60;
	private Integer refreshTokenValidity = 6 * 60 * 60;
	private String additionalInformation;
	private String autoApprove;
	private Date expirationTime;// 客户端过期时间
}
