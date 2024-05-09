package com.mahua.juanju.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdatePasswordRequest implements Serializable{
	private static final long serialVersionUID = 3191241716230486735L;
	private String oldPassword;
	private String newPassword;
	private String checkPassword;

}
