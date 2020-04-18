package com.gm.authorization.server.custom.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户与角色关系实体
 */
@Data
@Entity
@Table(name = "sys_user_role")
public class SysUserRole implements Serializable {
	private static final long serialVersionUID = -1810195806444298544L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "role_id")
	private Integer roleId;
}
