package com.gm.authorization.server.custom.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.io.Serializable;

/**
 * 角色实体
 */
@Data
@Entity
@Table(name = "sys_role")
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class SysRole extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -7136537864183138269L;

	@Column(name = "role_name")
	private String roleName;

	@Column(name = "role_code")
	private String roleCode;

	@Column(name = "role_description")
	private String roleDescription;

}
