package com.mahua.juanju.model.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍查询封装类
 */
@Data
@TableName(value ="team")
public class TeamQuery implements Serializable {
	/**
	 * 队伍id
	 */
	private Long id;

	/**
	 * 已加入的队伍id列表
	 */
	private List<Long> idList;

	/**
	 * 搜索关键词（同时对队伍名称和队伍描述生效）
	 */
	private String searchText;

	/**
	 * 队伍名称
	 */
	private String name;

	/**
	 * 队伍最大人数
	 */
	private Integer maxNum;

	/**
	 * 队伍描述
	 */
	private String description;

	/**
	 * 队伍过期时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private String expireTime;

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
	 * 分页查询的一个的个数
	 */
	private Integer pageSize = 8;

	/**
	 * 分页查询的页数
	 */
	private Integer pageNum;


}
