package com.mahua.juanju.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍和用户的信息封装类（脱敏），返回给前端
 */
@Data
public class TeamUserVO implements Serializable {

	private static final long serialVersionUID = 49479662056374828L;


	private Long id;

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
	 * 队伍过期时间
	 */
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

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	/**
	 * 创建人用户列表
	 */
	UserVO createUser;

	/**
	 * 已加入队伍的人数
	 */

	int hasJoinNum;
	private boolean hasJoin = false;

}
