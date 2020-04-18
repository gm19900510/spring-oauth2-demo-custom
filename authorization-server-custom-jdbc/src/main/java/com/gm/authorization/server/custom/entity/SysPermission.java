package com.gm.authorization.server.custom.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.io.Serializable;

/**
 * 功能权限实体
 */
@Data
@Entity
@Table(name = "sys_permission")
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class SysPermission extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 4285835478693487481L;

	@Column(name = "pid")
	private Integer pid;

	@Column(name = "type")
	private Integer type;

	@Column(name = "name")
	private String name;

	@Column(name = "code")
	private String code;

	@Column(name = "uri")
	private String uri;

	@Column(name = "seq")
	private Integer seq = 1;

}
