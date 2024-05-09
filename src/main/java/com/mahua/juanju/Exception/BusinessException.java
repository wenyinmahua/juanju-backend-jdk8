package com.mahua.juanju.Exception;


import com.mahua.juanju.common.ErrorCode;

/**
 * 自定义业务异常类
 *
 * mahua
 */
public class BusinessException extends RuntimeException {

	private final int code;
	private final String description;

	public BusinessException(int code, String msg, String description) {
		super(msg);
		this.code = code;
		this.description = description;
	}

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMsg());
		this.code = errorCode.getCode();
		this.description = errorCode.getDescription();
	}

	public BusinessException(ErrorCode errorCode,String description) {
		super(errorCode.getMsg());
		this.code = errorCode.getCode();
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
