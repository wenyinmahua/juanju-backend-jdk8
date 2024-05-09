package com.mahua.juanju.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamJoinRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 队伍 id
	 */
	private Long teamId;


	/**
	 * 加入队伍密码
	 */
	private String teamPassword;






}
