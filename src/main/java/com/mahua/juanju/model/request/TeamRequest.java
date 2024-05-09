package com.mahua.juanju.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamRequest implements Serializable {

	private static final long serialVersionUID = 3191241716230486735L;

	/**
	 * 队伍名称
	 */
	private String name;

	/**
	 * 队伍描述
	 */
	private String description;

	/**
	 * 队伍头像
	 */
	private String avatarUrl;

	/**
	 * 队伍最大人数
	 */
	private Integer maxNum;

	/**
	 * 加入队伍密码
	 */
	private String teamPassword;

	/**
	 * 队伍过期时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
	private Date expireTime;

	/**
	 * 创始人id
	 */
	private Long userId;

	/**
	 * 队伍分类
	 */
	private String category;

	/**
	 * 队伍状态：0-公开 1-私有 2-加密
	 */
	private Integer status;



}
