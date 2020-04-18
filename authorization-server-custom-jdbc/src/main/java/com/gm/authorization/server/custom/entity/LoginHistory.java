package com.gm.authorization.server.custom.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "t_login_history")
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class LoginHistory extends BaseEntity {
	private String ip;
	private String username;
	private String clientId;
	private String device;
	private Integer status;// 状态
}
