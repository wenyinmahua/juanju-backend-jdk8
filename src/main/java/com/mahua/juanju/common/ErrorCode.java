package com.mahua.juanju.common;

/**
 * 错误码
 *
 * @author mahua
 */
public enum ErrorCode {
	SUCCESS(0,"操作成功",""),
	PARAMS_ERROR(40000, "请求参数错误", ""),
	NULL_PARAMS(40001, "请求参数不能为空", ""),
	NO_LOGIN(40100, "未登录", ""),
	NO_AUTHORIZED(40101, "无权限", ""),
	FORBIDDEN(40301,"禁止操作",""),
//	USER_PASSWORD_ERROR(400, "密码错误", ""),
	USER_ACCOUNT_EXIST(40002, "账号已存在", ""),
	STU_ID_EXIST(40003, "学号已存在", ""),
	USER_NOT_EXIST(40004, "用户不存在", ""),
	USER_PASSWORD_ERROR(40005,"密码错误",""),
	USER_ACCOUNT_ERROR(40005, "账号或密码错误", ""),
	SYSTEM_ERROR(50000,"系统内部异常","")

//	USER_ACCOUNT_NOT_EXIST(400, "账号不存在", ""),
//	USER_ACCOUNT_LOCKED(400, "账号已被锁定", ""),
	;

	/**
	 * 状态码
	 */
	private final int code;
	/**
	 * 状态码信息
	 */
	private final String msg;
	/**
	 * 状态码描述（详情）
	 */
	private final String description;

	ErrorCode(int code, String msg, String description) {
		this.code = code;
		this.msg = msg;
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public String getDescription() {
		return description;
	}
}
