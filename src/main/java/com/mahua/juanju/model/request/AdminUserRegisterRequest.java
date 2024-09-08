package com.mahua.juanju.model.request;


import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author mahua
 */
@Data
public class AdminUserRegisterRequest implements Serializable {
	private static final long serialVersionUID = 3191241716230486735L;

	private String userAccount;

	private String username;

	private String stuId;

}
