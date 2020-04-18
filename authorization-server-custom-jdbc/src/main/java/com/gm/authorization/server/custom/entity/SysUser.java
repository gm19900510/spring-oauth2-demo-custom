package com.gm.authorization.server.custom.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.io.Serializable;

/**
 * 用户信息实体
 */
@Data
@Entity
@Table(name = "sys_user")
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class SysUser extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 5872438942257394882L;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "email")
	private String email;

}
